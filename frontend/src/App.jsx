import { useEffect, useState } from 'react';
import './App.css';

// Credentials stay in memory and are cleared on logout. Use HTTPS outside localhost.
export default function App() {
  const [user, setUser] = useState(null);
  const [authorization, setAuthorization] = useState('');
  const [register, setRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [tab, setTab] = useState('jobs');
  const [jobs, setJobs] = useState([]);
  const [applications, setApplications] = useState([]);
  const [analysis, setAnalysis] = useState(null);
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState(false);

  async function api(path, options = {}, auth = authorization) {
    const response = await fetch(`/api${path}`, {
      ...options,
      headers: { ...(auth ? { Authorization: auth } : {}), ...options.headers },
    });
    const text = await response.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    if (!response.ok) throw new Error(data?.error || (typeof data === 'string' && data) || `Request failed (${response.status})`);
    return data;
  }

  useEffect(() => {
    if (!authorization) return;
    let cancelled = false;
    Promise.all([api('/jobs'), api('/applications/mine')])
      .then(([vacancies, applied]) => { if (!cancelled) { setJobs(vacancies); setApplications(applied); } })
      .catch(error => { if (!cancelled) setMessage(error.message); });
    return () => { cancelled = true; };
    // Reload only when the authenticated account changes.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [authorization]);

  async function authenticate(event) {
    event.preventDefault(); setBusy(true); setMessage('');
    const cleanEmail = email.trim().toLowerCase();
    try {
      if (register) {
        await api('/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ name, email: cleanEmail, password }) }, '');
      }
      const result = await api('/auth/login', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ email: cleanEmail, password }) }, '');
      const bytes = new TextEncoder().encode(`${cleanEmail}:${password}`);
      const basic = `Basic ${btoa(String.fromCharCode(...bytes))}`;
      setAuthorization(basic); setUser(result); setPassword('');
    } catch (error) { setMessage(error.message); }
    finally { setBusy(false); }
  }

  function logout() {
    setAuthorization(''); setUser(null); setJobs([]); setApplications([]);
    setAnalysis(null); setPassword(''); setMessage(''); setTab('jobs');
  }

  async function upload(event) {
    const input = event.target;
    const file = input.files?.[0];
    if (!file) return;
    setAnalysis(null); setMessage('');
    if (!file.name.toLowerCase().endsWith('.pdf') || file.size > 10 * 1024 * 1024) {
      setMessage('Choose a PDF no larger than 10 MB.'); input.value = ''; return;
    }
    setBusy(true);
    try {
      const body = new FormData(); body.append('file', file);
      await api('/resumes/upload', { method: 'POST', body });
      setAnalysis(await api('/ai/resume-analysis/latest'));
      setMessage('Resume uploaded and scanned.');
    } catch (error) { setMessage(error.message); }
    finally { setBusy(false); input.value = ''; }
  }

  async function apply(jobId) {
    setBusy(true); setMessage('');
    try {
      await api('/applications/apply', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ jobId }) });
      setApplications(await api('/applications/mine'));
      setMessage('Application saved.');
    } catch (error) { setMessage(error.message); }
    finally { setBusy(false); }
  }

  const notice = message && <p role="status" className="dark-panel">{message}</p>;
  if (!user) return <div className="auth-root"><div className="auth-box">
    <h1 className="logo-title">SMART PLACEMENT <span className="red-accent">AI</span></h1>
    <p className="sub-tag">{register ? 'CREATE CANDIDATE ACCOUNT' : 'CANDIDATE LOGIN'}</p>
    {notice}
    <form onSubmit={authenticate}>
      {register && <label className="input-group">Full name<input className="dark-input" required maxLength={100} value={name} onChange={e => setName(e.target.value)} autoComplete="name" /></label>}
      <label className="input-group">Email<input className="dark-input" required type="email" value={email} onChange={e => setEmail(e.target.value)} autoComplete="username" /></label>
      <label className="input-group">Password<input className="dark-input" required minLength={register ? 8 : undefined} maxLength={72} type="password" value={password} onChange={e => setPassword(e.target.value)} autoComplete={register ? 'new-password' : 'current-password'} /></label>
      <button className="primary-red-btn-full" disabled={busy}>{busy ? 'Please wait…' : register ? 'Create account' : 'Log in'}</button>
    </form>
    <button className="nav-btn" disabled={busy} onClick={() => { setRegister(!register); setMessage(''); }}>{register ? 'Already registered? Log in' : 'Create an account'}</button>
    <p>Portfolio project · Java, Spring Boot, Maven and React</p>
  </div></div>;

  return <div className="portal-root">
    <header className="main-header"><h1 className="logo-title-header">SMART PLACEMENT AI</h1>
      <nav className="nav-group" aria-label="Main navigation">{['jobs', 'resume', 'applications'].map(item => <button key={item} className={tab === item ? 'nav-btn active' : 'nav-btn'} onClick={() => setTab(item)}>{item.toUpperCase()}</button>)}</nav>
      <div className="user-controls"><span>{user.name}</span><button className="logout-btn" onClick={logout} disabled={busy}>LOGOUT</button></div>
    </header>
    <main className="section-container">{notice}
      {tab === 'jobs' && <><h2 className="section-header">Active opportunities</h2><p>Demo vacancies are explicitly labelled. No company partnership is implied.</p><div className="grid-2">
        {jobs.length === 0 && <p>No active jobs available.</p>}
        {jobs.map(job => <article className="dark-panel" key={job.id}><h3>{job.companyName}</h3><p>{job.role} · {job.location}</p><p>{job.description}</p><p>Skills: {job.requiredSkills}</p>
          <button className="action-red-btn" disabled={busy || applications.some(a => a.jobId === job.id)} onClick={() => apply(job.id)}>{applications.some(a => a.jobId === job.id) ? 'Applied ✓' : 'Apply'}</button></article>)}
      </div></>}
      {tab === 'resume' && <><h2 className="section-header">Resume keyword scanner</h2><div className="dark-panel"><p>Upload a text-based PDF, up to 10 MB. This scanner detects known skill keywords; it does not provide an ATS score, AI evaluation, or guaranteed job match. Scanned images require OCR, which is not supported.</p>
        <label htmlFor="resume">Resume PDF</label><input id="resume" type="file" accept=".pdf,application/pdf" disabled={busy} onChange={upload} />
        {busy && <p>Processing…</p>}
        {analysis && <section><h3>Detected keywords</h3><div className="skills-wrap">{analysis.technicalSkills ? analysis.technicalSkills.split(',').map(skill => <span className="red-skill-tag" key={skill}>{skill}</span>) : <p>No keywords from the scanner vocabulary were found.</p>}</div><p>{analysis.summary}</p><p>{analysis.recommendations}</p></section>}
      </div></>}
      {tab === 'applications' && <><h2 className="section-header">Your applications</h2>{applications.length === 0 ? <p>No applications yet.</p> : applications.map(a => <article className="dark-panel" key={a.id}><h3>{jobs.find(job => job.id === a.jobId)?.role || `Job #${a.jobId}`}</h3><p>Status: {a.status}</p><p>Applied: {new Date(a.appliedAt).toLocaleString()}</p></article>)}</>}
    </main><footer className="main-footer">Smart Placement AI · Student portfolio project</footer>
  </div>;
}
