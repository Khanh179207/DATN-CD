<template>
  <Teleport to="body">
    <div v-if="modelValue" class="modal-overlay" @click.self="$emit('update:modelValue', false)">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Reject Post</h3>
          <button class="close-btn" @click="$emit('update:modelValue', false)">✕</button>
        </div>
        <div class="modal-body">
          <p class="post-title">{{ postTitle }}</p>

          <label class="field-label">Rejection Reason Code <span class="req">*</span></label>
          <select v-model="form.code" class="select-input">
            <option value="">— Select code —</option>
            <option v-for="opt in CODES" :key="opt.value" :value="opt.value">{{ opt.label }}</option>
          </select>

          <label class="field-label mt">Additional Note (shown to author)</label>
          <textarea
            v-model="form.reason"
            class="textarea-input"
            rows="4"
            placeholder="Explain why the content was rejected (optional but recommended)."
          />
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="$emit('update:modelValue', false)">Cancel</button>
          <button class="btn-reject" :disabled="!form.code || submitting" @click="submit">
            <span v-if="submitting">Rejecting…</span>
            <span v-else>Reject Post</span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  postTitle:  { type: String, default: '' },
  postId:     { type: Number, required: true },
})

const emit = defineEmits(['update:modelValue', 'confirmed'])

const CODES = [
  { value: 'SPAM',          label: 'Spam / Promotional content' },
  { value: 'INAPPROPRIATE', label: 'Inappropriate / offensive content' },
  { value: 'OFF_TOPIC',     label: 'Off-topic / not a recipe' },
  { value: 'DUPLICATE',     label: 'Duplicate of an existing post' },
  { value: 'LOW_QUALITY',   label: 'Low quality / incomplete' },
  { value: 'OTHER',         label: 'Other (see note)' },
]

const form = ref({ code: '', reason: '' })
const submitting = ref(false)

// Reset form when modal opens
watch(() => props.modelValue, (open) => {
  if (open) form.value = { code: '', reason: '' }
})

async function submit() {
  if (!form.value.code) return
  submitting.value = true
  try {
    emit('confirmed', { id: props.postId, code: form.value.code, reason: form.value.reason })
    emit('update:modelValue', false)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed; inset: 0;
  background: rgba(0,0,0,.45);
  display: flex; align-items: center; justify-content: center;
  z-index: 9999;
}
.modal-card {
  background: #fff;
  border-radius: 12px;
  width: 480px;
  max-width: calc(100vw - 32px);
  box-shadow: 0 20px 60px rgba(0,0,0,.25);
  display: flex; flex-direction: column;
}
.modal-header {
  padding: 18px 20px 12px;
  display: flex; align-items: center; justify-content: space-between;
  border-bottom: 1px solid #e5e7eb;
}
.modal-header h3 { margin: 0; font-size: 1rem; font-weight: 700; color: #dc2626; }
.close-btn { background: none; border: none; cursor: pointer; font-size: 1.1rem; color: #6b7280; }
.close-btn:hover { color: #111; }
.modal-body { padding: 18px 20px; display: flex; flex-direction: column; gap: 6px; }
.post-title { font-weight: 600; color: #374151; font-size: .9rem; margin-bottom: 8px; }
.field-label { font-size: .8rem; font-weight: 600; color: #374151; }
.field-label.mt { margin-top: 12px; }
.req { color: #ef4444; }
.select-input, .textarea-input {
  width: 100%; padding: 8px 10px;
  border: 1px solid #d1d5db; border-radius: 8px;
  font-size: .88rem; color: #111;
  background: #f9fafb;
  resize: vertical;
}
.select-input:focus, .textarea-input:focus { outline: 2px solid #ef4444; border-color: transparent; }
.modal-footer {
  padding: 12px 20px;
  display: flex; justify-content: flex-end; gap: 10px;
  border-top: 1px solid #e5e7eb;
}
.btn-cancel {
  padding: 8px 18px; border-radius: 8px;
  border: 1px solid #d1d5db; background: #fff;
  cursor: pointer; font-size: .88rem;
}
.btn-cancel:hover { background: #f3f4f6; }
.btn-reject {
  padding: 8px 18px; border-radius: 8px;
  border: none; background: #dc2626; color: #fff;
  cursor: pointer; font-size: .88rem; font-weight: 600;
}
.btn-reject:disabled { opacity: .55; cursor: not-allowed; }
.btn-reject:not(:disabled):hover { background: #b91c1c; }
</style>
