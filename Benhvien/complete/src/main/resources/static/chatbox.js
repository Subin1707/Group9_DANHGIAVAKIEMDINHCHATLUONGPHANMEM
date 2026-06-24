(() => {
  const STORAGE_KEY = 'hospital-chat-history-v2';
  const panel = document.getElementById('chatbox-panel');
  const toggle = document.getElementById('chatbox-toggle');
  const close = document.getElementById('chatbox-close');
  const reset = document.getElementById('chatbox-reset');
  const form = document.getElementById('chatbox-form');
  const input = document.getElementById('chatbox-input');
  const send = document.getElementById('chatbox-send');
  const messages = document.getElementById('chatbox-messages');
  const suggestions = document.querySelectorAll('.chatbox-suggestions button');
  if (!panel || !toggle || !form) return;

  let history = loadHistory();
  history.forEach(item => appendMessage(item.content, item.role));

  toggle.addEventListener('click', () => {
    const opening = panel.hidden;
    panel.hidden = !opening;
    toggle.setAttribute('aria-expanded', String(opening));
    if (opening) setTimeout(() => input.focus(), 0);
  });
  close.addEventListener('click', () => {
    panel.hidden = true;
    toggle.setAttribute('aria-expanded', 'false');
    toggle.focus();
  });
  reset.addEventListener('click', () => {
    history = [];
    sessionStorage.removeItem(STORAGE_KEY);
    messages.querySelectorAll('.chat-message:not(:first-child)').forEach(node => node.remove());
  });
  suggestions.forEach(button => button.addEventListener('click', () => {
    input.value = button.textContent;
    form.requestSubmit();
  }));
  input.addEventListener('input', resizeInput);
  input.addEventListener('keydown', event => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      form.requestSubmit();
    }
  });

  form.addEventListener('submit', async event => {
    event.preventDefault();
    const text = input.value.trim();
    if (!text || send.disabled) return;

    const requestHistory = history.slice(-10);
    appendMessage(text, 'user');
    remember('user', text);
    input.value = '';
    resizeInput();
    send.disabled = true;
    const loading = appendLoading();

    try {
      const response = await fetch('/api/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: text, history: requestHistory })
      });
      const data = await response.json().catch(() => ({}));
      if (!response.ok) throw new Error(data.error || 'Máy chủ chatbot không phản hồi.');
      appendMessage(data.reply, 'assistant');
      remember('assistant', data.reply);
    } catch (error) {
      appendMessage(error.message || 'Không thể kết nối tới chatbot.', 'error');
    } finally {
      loading.remove();
      send.disabled = false;
      input.focus();
    }
  });

  function appendMessage(text, role) {
    const row = document.createElement('div');
    row.className = `chat-message ${role}`;
    if (role === 'assistant') {
      const avatar = document.createElement('span');
      avatar.className = 'message-avatar';
      avatar.textContent = 'AI';
      row.appendChild(avatar);
    }
    const bubble = document.createElement('div');
    bubble.className = 'message-bubble';
    bubble.textContent = text;
    row.appendChild(bubble);
    messages.appendChild(row);
    scrollToBottom();
    return row;
  }

  function appendLoading() {
    const row = document.createElement('div');
    row.className = 'chat-message assistant';
    row.innerHTML = '<span class="message-avatar">AI</span><div class="message-bubble"><span class="typing"><i></i><i></i><i></i></span></div>';
    messages.appendChild(row);
    scrollToBottom();
    return row;
  }

  function remember(role, content) {
    history.push({ role, content });
    history = history.slice(-20);
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(history));
  }

  function loadHistory() {
    try {
      const saved = JSON.parse(sessionStorage.getItem(STORAGE_KEY) || '[]');
      return Array.isArray(saved) ? saved.filter(item => item && ['user', 'assistant'].includes(item.role) && typeof item.content === 'string') : [];
    } catch (_) {
      return [];
    }
  }

  function resizeInput() {
    input.style.height = 'auto';
    input.style.height = `${Math.min(input.scrollHeight, 100)}px`;
  }

  function scrollToBottom() {
    requestAnimationFrame(() => { messages.scrollTop = messages.scrollHeight; });
  }
})();
