<template>
  <Teleport to="body">
    <transition name="danger-modal-fade">
      <div v-if="modelValue" class="danger-modal-overlay" @click.self="emit('cancel')">
        <div class="danger-modal-card" role="dialog" aria-modal="true" @click.stop>
          <button type="button" class="danger-modal-close" @click="emit('cancel')">×</button>

          <div class="danger-modal-eyebrow">{{ eyebrow }}</div>
          <h3>{{ title }}</h3>
          <p>{{ description }}</p>

          <ul v-if="highlights.length" class="danger-modal-highlights">
            <li v-for="item in highlights" :key="item">{{ item }}</li>
          </ul>

          <div class="danger-modal-actions">
            <button type="button" class="danger-modal-secondary" @click="emit('cancel')">
              {{ cancelLabel }}
            </button>
            <button type="button" class="danger-modal-primary" :disabled="loading" @click="emit('confirm')">
              {{ loading ? loadingLabel : confirmLabel }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<script setup>
const props = defineProps({
  modelValue: { type: Boolean, default: false },
  eyebrow: { type: String, default: 'Confirm security action' },
  title: { type: String, required: true },
  description: { type: String, required: true },
  highlights: { type: Array, default: () => [] },
  confirmLabel: { type: String, default: 'Confirm' },
  cancelLabel: { type: String, default: 'Cancel' },
  loadingLabel: { type: String, default: 'Processing...' },
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'cancel', 'confirm'])

void props
</script>

<style scoped lang="scss">
.danger-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 1200;
  background: rgba(15, 23, 42, 0.62);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 18px;
}

.danger-modal-card {
  width: min(100%, 460px);
  padding: 24px;
  border-radius: 24px;
  background:
    radial-gradient(circle at top right, rgba(251, 191, 36, 0.16), transparent 28%),
    linear-gradient(180deg, #ffffff, #fff7ed);
  border: 1px solid rgba(251, 146, 60, 0.24);
  box-shadow: 0 30px 80px rgba(15, 23, 42, 0.22);
  position: relative;

  h3 {
    margin: 6px 0 8px;
    font-size: 20px;
    color: #0f172a;
  }

  p {
    margin: 0;
    color: #475569;
    line-height: 1.6;
  }
}

.danger-modal-eyebrow {
  display: inline-flex;
  padding: 5px 10px;
  border-radius: 999px;
  background: #fff7ed;
  color: #c2410c;
  border: 1px solid #fdba74;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.danger-modal-close {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.9);
  color: #64748b;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}

.danger-modal-highlights {
  margin: 16px 0 0;
  padding: 0;
  list-style: none;
  display: grid;
  gap: 8px;

  li {
    padding: 10px 12px;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.72);
    border: 1px solid rgba(226, 232, 240, 0.9);
    color: #334155;
    font-size: 13px;
  }
}

.danger-modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.danger-modal-secondary,
.danger-modal-primary {
  border: none;
  border-radius: 12px;
  padding: 11px 16px;
  font-weight: 700;
  cursor: pointer;
}

.danger-modal-secondary {
  background: #ffffff;
  color: #334155;
  border: 1px solid #cbd5e1;
}

.danger-modal-primary {
  background: linear-gradient(135deg, #ef4444, #b91c1c);
  color: #fff;
  box-shadow: 0 14px 24px rgba(220, 38, 38, 0.24);

  &:disabled {
    opacity: 0.7;
    cursor: wait;
  }
}

.danger-modal-fade-enter-active,
.danger-modal-fade-leave-active {
  transition: opacity 0.18s ease;
}

.danger-modal-fade-enter-from,
.danger-modal-fade-leave-to {
  opacity: 0;
}

@media (max-width: 640px) {
  .danger-modal-card {
    padding: 20px;
    border-radius: 20px;
  }

  .danger-modal-actions {
    flex-direction: column-reverse;
  }
}
</style>