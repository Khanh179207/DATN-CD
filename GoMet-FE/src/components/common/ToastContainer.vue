<template>
  <Teleport to="body">
    <div class="toast-portal" aria-live="polite" aria-atomic="false">
      <transition-group name="toast" tag="div" class="toast-stack">
        <div
          v-for="t in toasts"
          :key="t.id"
          class="toast-item"
          :class="[`toast--${t.type}`, { 'toast--visible': t.visible }]"
          role="alert"
          @click="dismiss(t.id)"
        >
          <span class="toast-icon">{{ t.icon }}</span>
          <span class="toast-msg">{{ t.message }}</span>
          <button class="toast-close" @click.stop="dismiss(t.id)" aria-label="Close">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round"><line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/></svg>
          </button>
          <div class="toast-progress"></div>
        </div>
      </transition-group>
    </div>
  </Teleport>
</template>

<script setup>
import { useToast } from '@/composables/useToast'
const { toasts, dismiss } = useToast()
</script>

<style lang="scss">
@use '../../assets/styles/variables' as *;

.toast-portal {
  position: fixed;
  bottom: 28px;
  right: 28px;
  z-index: 99999;
  pointer-events: none;
}

.toast-stack {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
}

.toast-item {
  pointer-events: all;
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 280px;
  max-width: 400px;
  padding: 13px 16px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.14), 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #ccc;
  font-family: 'Mulish', sans-serif;
  font-size: 0.88rem;
  font-weight: 600;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: transform 0.25s ease, opacity 0.25s ease;

  &.toast--success { border-left-color: #16a34a; .toast-icon { color: #16a34a; } }
  &.toast--error   { border-left-color: #dc2626; .toast-icon { color: #dc2626; } }
  &.toast--warn    { border-left-color: #d97706; .toast-icon { color: #d97706; } }
  &.toast--info    { border-left-color: #EA580C;  .toast-icon { color: #EA580C;  } }

  &:hover { transform: translateX(-4px); }
}

.toast-icon {
  font-size: 1.1rem;
  font-weight: 900;
  flex-shrink: 0;
  width: 22px;
  text-align: center;
}

.toast-msg {
  flex: 1;
  color: #1C1917;
  line-height: 1.4;
}

.toast-close {
  background: none;
  border: none;
  cursor: pointer;
  padding: 2px;
  color: #A8A29E;
  border-radius: 4px;
  display: flex;
  align-items: center;
  flex-shrink: 0;
  transition: color 0.2s;
  &:hover { color: #1C1917; }
}

.toast-progress {
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 100%;
  background: currentColor;
  opacity: 0.15;
  animation: toastProgress 5s linear forwards;
}

@keyframes toastProgress {
  from { width: 100%; }
  to   { width: 0%; }
}

/* transition-group animations */
.toast-enter-active { transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1); }
.toast-leave-active { transition: all 0.3s ease; }
.toast-enter-from   { opacity: 0; transform: translateX(60px) scale(0.9); }
.toast-leave-to     { opacity: 0; transform: translateX(60px) scale(0.9); }
.toast-move         { transition: transform 0.3s ease; }
</style>
