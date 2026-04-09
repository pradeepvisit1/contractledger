// ── CONFIG ────────────────────────────────────────────────────────
const API = window.location.origin + '/api';

// ── STATE ─────────────────────────────────────────────────────────
let TK = localStorage.getItem('cl_tk');
let CU = JSON.parse(localStorage.getItem('cl_cu') || 'null');
let AP = JSON.parse(localStorage.getItem('cl_ap') || 'null');

// ── HELPERS ───────────────────────────────────────────────────────
const fmt   = n  => '₹' + Number(n || 0).toLocaleString('en-IN');
const fmtD  = d  => { if (!d) return '—'; try { return new Date(d).toLocaleDateString('en-IN', {day:'2-digit',month:'short',year:'2-digit'}); } catch(e) { return d; } };
const today = () => new Date().toISOString().split('T')[0];
const isAdmin = () => CU?.role === 'ADMIN';

const CAT_COLOR = { MATERIAL:'#3b82f6', LABOR:'#22c55e', MACHINERY:'#f59e0b', TRANSPORT:'#ef4444', OTHER:'#a855f7' };
const CAT_ICON  = { MATERIAL:'🪨', LABOR:'👷', MACHINERY:'⚙️', TRANSPORT:'🚛', OTHER:'📌' };
const CAT_BADGE = { MATERIAL:'badge-blue', LABOR:'badge-green', MACHINERY:'badge-warn', TRANSPORT:'badge-red', OTHER:'badge-purple' };

// ── API FETCH (fixed) ─────────────────────────────────────────────
async function api(method, path, body) {
  try {
    const r = await fetch(API + path, {
      method,
      headers: {
        'Content-Type': 'application/json',
        ...(TK ? { Authorization: 'Bearer ' + TK } : {})
      },
      body: body ? JSON.stringify(body) : undefined
    });

    if (r.status === 401) { doLogout(true); return null; }

    if (!r.ok) {
      const t = await r.text();
      toast('❌ ' + (t || 'Error ' + r.status), 'error');
      return null;
    }

    // ── Safe response parsing ──────────────────────────────────────
    const text = await r.text();

    // Empty body → success
    if (!text || text.trim() === '') return true;

    // Try JSON first
    try {
      return JSON.parse(text);
    } catch (_) {
      // Plain text response like "Investor added", "Deleted", "OK"
      // These are success responses — return true so callers work correctly
      return true;
    }

  } catch (e) {
    // Only real network errors reach here (server down, no internet)
    toast('❌ Cannot reach server — is Spring Boot running?', 'error');
    console.error(e);
    return null;
  }
}

// ── TOAST ─────────────────────────────────────────────────────────
function toast(msg, type = '') {
  let t = document.getElementById('toast');
  if (!t) { t = document.createElement('div'); t.id = 'toast'; t.className = 'toast'; document.body.appendChild(t); }
  t.textContent = msg;
  t.className = 'toast ' + type;
  t.classList.add('show');
  clearTimeout(t._to);
  t._to = setTimeout(() => t.classList.remove('show'), 2600);
}

// ── AUTH ──────────────────────────────────────────────────────────
function doLogout(silent = false) {
  if (!silent && !confirm('Sign out?')) return;
  TK = null; CU = null; AP = null;
  ['cl_tk', 'cl_cu', 'cl_ap'].forEach(k => localStorage.removeItem(k));
  window.location.href = '/index.html';
}

function requireAuth() {
  if (!TK || !CU) { window.location.href = '/index.html'; return false; }
  return true;
}

// ── HEADER RENDER ─────────────────────────────────────────────────
function renderHeader(activePage) {
  const projName = AP ? AP.name : 'No project selected';
  const pages = [
    { id:'dashboard',  label:'Dashboard', icon:'📊', href:'dashboard.html' },
    { id:'investors',  label:'Investors', icon:'💰', href:'investors.html' },
    { id:'expenses',   label:'Expenses',  icon:'📋', href:'expenses.html' },
    { id:'workers',    label:'Workers',   icon:'👷', href:'workers.html' },
    { id:'reports',    label:'Reports',   icon:'📈', href:'reports.html' },
  ];

  document.getElementById('hproj').textContent = projName;
  document.getElementById('uchip').innerHTML =
    `<span>${CU?.fullName || CU?.username || 'user'}</span>
     <span class="role-badge ${CU?.role === 'ADMIN' ? '' : 'user'}">${CU?.role || 'USER'}</span>`;

  // Sidebar
  const sb = document.getElementById('sidebar');
  if (sb) {
    sb.innerHTML = pages.map(p =>
      `<a class="nav-link ${activePage === p.id ? 'active' : ''}" href="${p.href}">
         <span class="ni">${p.icon}</span>${p.label}
       </a>`
    ).join('') +
    `<hr class="divider" style="margin-top:auto">
     <button class="nav-link" onclick="doLogout()" style="color:var(--danger)">
       <span class="ni">🚪</span>Sign Out
     </button>`;
  }

  // Mobile tabs
  const mt = document.getElementById('mob-tabs-inner');
  if (mt) {
    mt.innerHTML = pages.map(p =>
      `<a class="mob-tab ${activePage === p.id ? 'active' : ''}" href="${p.href}">
         <span class="mi">${p.icon}</span>${p.label}
       </a>`
    ).join('');
  }
}

// ── PROJECT HELPERS ────────────────────────────────────────────────
function saveProject(p) {
  AP = p;
  localStorage.setItem('cl_ap', JSON.stringify(p));
}

async function loadProjectsIntoModal() {
  const list = await api('GET', '/projects') || [];
  const el = document.getElementById('proj-list-modal');
  if (!el) return list;

  if (!list.length) {
    el.innerHTML = '<div style="font-size:13px;color:var(--text3);padding:8px 0">No projects yet. Create one above.</div>';
    return list;
  }

  el.innerHTML = list.map(p => `
    <div class="list-item" style="${p.id === AP?.id ? 'border-color:var(--accent);background:var(--accent-lt)' : ''}">
      <div class="li-left" style="cursor:pointer" onclick="selectProject(${p.id})">
        <div class="li-title">${p.name} ${p.id === AP?.id ? '<span class="badge badge-blue">Active</span>' : ''}</div>
        <div class="li-sub">${p.type || 'N/A'} · ${p.location || '—'}</div>
      </div>
      <div style="display:flex;gap:6px;align-items:center">
        <button class="btn btn-ghost btn-sm" onclick="selectProject(${p.id})">Select</button>
        ${isAdmin() ? `<button class="btn btn-danger btn-sm" onclick="deleteProject(${p.id})">🗑️</button>` : ''}
      </div>
    </div>`).join('');
  return list;
}

async function selectProject(id) {
  const list = await api('GET', '/projects') || [];
  const p = list.find(x => x.id === id);
  if (p) { saveProject(p); window.location.reload(); }
}

async function deleteProject(id) {
  if (!isAdmin()) return toast('Admin only', 'error');
  if (!confirm('Delete project and ALL its data? This cannot be undone.')) return;
  await api('DELETE', '/projects/' + id);
  if (AP?.id === id) { AP = null; localStorage.removeItem('cl_ap'); }
  await loadProjectsIntoModal();
  toast('Project deleted');
}

// ── MODAL HELPERS ──────────────────────────────────────────────────
function openModal(id)  { document.getElementById(id).classList.add('open'); }
function closeModal(id) { document.getElementById(id).classList.remove('open'); }

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.mo').forEach(o =>
    o.addEventListener('click', e => { if (e.target === o) o.classList.remove('open'); })
  );
});

// ── NO PROJECT GUARD ───────────────────────────────────────────────
function checkProject() {
  if (!AP) { toast('⚠️ Select a project first', 'error'); openModal('proj-modal'); return false; }
  return true;
}
