<template>
  <Teleport to="body">
    <Transition name="modal-fade">
      <div v-if="show" class="overlay" @click.self="$emit('close')">
        <div class="compose-card">
          <!-- Header -->
          <div class="compose-header">
            <div class="ch-left">
              <div class="ch-icon"><i class="fa-solid fa-envelope"></i></div>
              <div>
                <h3 class="ch-title">Gửi Email</h3>
                <p class="ch-sub">Gửi tới {{ recipients.length }} người dùng</p>
              </div>
            </div>
            <button class="close-btn" @click="$emit('close')" :disabled="sending">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </div>

          <!-- Recipients preview -->
          <div class="recipients-bar">
            <i class="fa-solid fa-users rec-icon"></i>
            <div class="rec-chips">
              <span
                v-for="r in displayedRecipients"
                :key="r.userId || r.email"
                class="rec-chip"
              >
                {{ r.username || r.email }}
              </span>
              <span v-if="recipients.length > MAX_SHOWN" class="rec-chip rec-more">
                +{{ recipients.length - MAX_SHOWN }} người khác
              </span>
            </div>
          </div>

          <!-- Form body -->
          <div class="compose-body">
            <!-- Subject -->
            <div class="field-group">
              <label class="field-label">Tiêu đề <span class="required">*</span></label>
              <input
                v-model="form.subject"
                type="text"
                class="field-input"
                :class="{ 'input-error': v.subject }"
                placeholder="Nhập tiêu đề email..."
                maxlength="255"
              />
              <span v-if="v.subject" class="err-msg">{{ v.subject }}</span>
            </div>

            <!-- HTML Body -->
            <div class="field-group">
              <label class="field-label">
                Nội dung <span class="required">*</span>
                <span class="field-hint">(hỗ trợ HTML cơ bản)</span>
              </label>
              <textarea
                v-model="form.bodyHtml"
                class="field-textarea"
                :class="{ 'input-error': v.bodyHtml }"
                placeholder="Nội dung email của bạn…&#10;&#10;Ví dụ:&#10;<b>Xin chào!</b>&#10;<p>Đây là thông báo từ GoMet team.</p>"
                rows="10"
              ></textarea>
              <span v-if="v.bodyHtml" class="err-msg">{{ v.bodyHtml }}</span>
              <div class="char-count">{{ form.bodyHtml.length }} ký tự</div>
            </div>

            <!-- Preview toggle -->
            <div class="preview-section">
              <button class="preview-toggle" @click="showPreview = !showPreview" type="button">
                <i :class="showPreview ? 'fa-solid fa-eye-slash' : 'fa-solid fa-eye'"></i>
                {{ showPreview ? 'Ẩn xem trước' : 'Xem trước email' }}
              </button>
              <Transition name="slide-down">
                <div v-if="showPreview" class="preview-frame" v-html="sanitizedPreview"></div>
              </Transition>
            </div>
          </div>

          <!-- Footer -->
          <div class="compose-footer">
            <div class="footer-info" v-if="sending">
              <div class="mini-spinner"></div>
              <span>Đang gửi {{ recipients.length }} email…</span>
            </div>
            <div class="footer-actions">
              <button class="btn-cancel" @click="$emit('close')" :disabled="sending">Hủy</button>
              <button class="btn-draft" @click="handleDraft" :disabled="sending">
                <i class="fa-regular fa-floppy-disk"></i> Lưu nháp
              </button>
              <button class="btn-send" @click="handleSend" :disabled="sending">
                <i class="fa-solid fa-paper-plane"></i>
                {{ sending ? 'Đang gửi…' : `Gửi cho ${recipients.length} người` }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { createDraft, createAndSend } from '@/services/adminEmailService'
import { toast } from '@/composables/useToast'

const props = defineProps({
  /** Array of { userId?, email, username? } */
  recipients: { type: Array, default: () => [] },
  show: { type: Boolean, default: false }
})

const emit = defineEmits(['close', 'sent', 'drafted'])

const MAX_SHOWN = 8

const form = ref({ subject: '', bodyHtml: '' })
const v = ref({})
const sending = ref(false)
const showPreview = ref(false)

const displayedRecipients = computed(() => props.recipients.slice(0, MAX_SHOWN))

// Basic HTML sanitization for preview (strip scripts)
const sanitizedPreview = computed(() => {
  return form.value.bodyHtml
    .replace(/<script[\s\S]*?<\/script>/gi, '')
    .replace(/on\w+="[^"]*"/gi, '')
})

// Reset when modal opens
watch(() => props.show, (val) => {
  if (val) {
    form.value = { subject: '', bodyHtml: '' }
    v.value = {}
    showPreview.value = false
    sending.value = false
  }
})

function validate () {
  const errors = {}
  if (!form.value.subject.trim()) errors.subject = 'Vui lòng nhập tiêu đề.'
  if (!form.value.bodyHtml.trim()) errors.bodyHtml = 'Vui lòng nhập nội dung.'
  v.value = errors
  return Object.keys(errors).length === 0
}

function buildPayload () {
  const payload = {
    subject: form.value.subject.trim(),
    bodyHtml: form.value.bodyHtml.trim(),
  }
  const userIds = props.recipients.filter(r => r.userId).map(r => r.userId)
  const emails  = props.recipients.filter(r => !r.userId && r.email).map(r => r.email)
  if (userIds.length) payload.recipientUserIds = userIds
  if (emails.length)  payload.recipientEmails  = emails
  return payload
}

async function handleSend () {
  if (!validate()) return
  sending.value = true
  try {
    const res = await createAndSend(buildPayload())
    toast.success(`Đã xếp hàng ${props.recipients.length} email thành công!`)
    emit('sent', res.data)
    emit('close')
  } catch (err) {
    const msg = err.response?.data?.message || err.response?.data || 'Gửi email thất bại!'
    toast.error(String(msg))
  } finally {
    sending.value = false
  }
}

async function handleDraft () {
  if (!validate()) return
  sending.value = true
  try {
    const res = await createDraft(buildPayload())
    toast.success('Đã lưu email nháp.')
    emit('drafted', res.data)
    emit('close')
  } catch (err) {
    const msg = err.response?.data?.message || err.response?.data || 'Lưu nháp thất bại!'
    toast.error(String(msg))
  } finally {
    sending.value = false
  }
}
</script>

<style scoped>
/* Overlay */
.overlay {
  position: fixed; inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 10000;
  display: flex; align-items: center; justify-content: center;
  backdrop-filter: blur(4px);
  padding: 20px;
}

/* Card */
.compose-card {
  background: white;
  border-radius: 20px;
  width: 100%; max-width: 660px;
  max-height: 90vh;
  display: flex; flex-direction: column;
  box-shadow: 0 30px 80px rgba(0, 0, 0, 0.25);
  animation: cardPop 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  overflow: hidden;
}
@keyframes cardPop {
  from { opacity: 0; transform: scale(0.88); }
  to   { opacity: 1; transform: scale(1); }
}

/* Header */
.compose-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 22px 28px 18px;
  border-bottom: 1px solid #F1F5F9;
  background: linear-gradient(135deg, #FFF7ED, #FFFBF5);
  flex-shrink: 0;
}
.ch-left { display: flex; align-items: center; gap: 14px; }
.ch-icon {
  width: 44px; height: 44px; border-radius: 12px;
  background: linear-gradient(135deg, #F97316, #EA580C);
  display: flex; align-items: center; justify-content: center;
  color: white; font-size: 1.1rem;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.35);
}
.ch-title { font-size: 1.1rem; font-weight: 800; color: #0F172A; margin: 0 0 2px; }
.ch-sub   { font-size: 0.8rem; color: #64748B; margin: 0; }
.close-btn {
  width: 34px; height: 34px; border-radius: 50%;
  border: 1px solid #E2E8F0; background: white;
  display: flex; align-items: center; justify-content: center;
  color: #94A3B8; cursor: pointer; transition: 0.2s;
  font-size: 0.9rem;
}
.close-btn:hover { background: #FEE2E2; color: #EF4444; border-color: #FEE2E2; }
.close-btn:disabled { opacity: 0.4; cursor: not-allowed; }

/* Recipients bar */
.recipients-bar {
  display: flex; align-items: flex-start; gap: 10px;
  padding: 14px 28px;
  background: #F8FAFC;
  border-bottom: 1px solid #F1F5F9;
  flex-shrink: 0;
}
.rec-icon { color: #64748B; font-size: 0.85rem; margin-top: 4px; }
.rec-chips { display: flex; flex-wrap: wrap; gap: 6px; flex: 1; }
.rec-chip {
  background: #EFF6FF; color: #2563EB;
  border: 1px solid #BFDBFE;
  padding: 3px 10px; border-radius: 20px;
  font-size: 0.75rem; font-weight: 700;
}
.rec-more {
  background: #F1F5F9; color: #64748B;
  border-color: #E2E8F0;
}

/* Body */
.compose-body {
  flex: 1; overflow-y: auto;
  padding: 22px 28px;
  display: flex; flex-direction: column; gap: 18px;
}

.field-group { display: flex; flex-direction: column; gap: 6px; }
.field-label {
  font-size: 0.8rem; font-weight: 800;
  color: #374151; text-transform: uppercase; letter-spacing: 0.5px;
  display: flex; align-items: center; gap: 6px;
}
.required { color: #EF4444; }
.field-hint { font-size: 0.75rem; color: #94A3B8; font-weight: 500; text-transform: none; letter-spacing: 0; }

.field-input, .field-textarea {
  width: 100%; box-sizing: border-box;
  border: 1.5px solid #E2E8F0; border-radius: 10px;
  padding: 10px 14px;
  font-size: 0.9rem; font-family: 'Mulish', sans-serif;
  color: #1E293B; background: white;
  transition: 0.2s; outline: none; resize: none;
}
.field-input:focus, .field-textarea:focus {
  border-color: #F97316;
  box-shadow: 0 0 0 3px rgba(249, 115, 22, 0.12);
}
.input-error { border-color: #EF4444 !important; }
.err-msg { font-size: 0.77rem; color: #EF4444; font-weight: 600; }
.char-count { font-size: 0.72rem; color: #94A3B8; text-align: right; }

.field-textarea { font-size: 0.85rem; line-height: 1.6; }

/* Preview */
.preview-section { display: flex; flex-direction: column; gap: 10px; }
.preview-toggle {
  align-self: flex-start;
  background: none; border: 1px solid #E2E8F0;
  border-radius: 8px; padding: 6px 14px;
  font-size: 0.8rem; font-weight: 700; color: #64748B;
  cursor: pointer; display: flex; align-items: center; gap: 7px;
  transition: 0.2s; font-family: 'Mulish', sans-serif;
}
.preview-toggle:hover { background: #F8FAFC; border-color: #CBD5E1; color: #374151; }

.preview-frame {
  border: 1px solid #E2E8F0; border-radius: 10px;
  padding: 20px 22px; background: #FAFAFA;
  font-size: 0.9rem; line-height: 1.7; color: #374151;
  max-height: 260px; overflow-y: auto;
  word-break: break-word;
}

/* Footer */
.compose-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 28px 22px;
  border-top: 1px solid #F1F5F9;
  flex-shrink: 0; gap: 12px;
}
.footer-info { display: flex; align-items: center; gap: 8px; font-size: 0.85rem; color: #64748B; font-weight: 600; }
.mini-spinner {
  width: 16px; height: 16px;
  border: 2px solid #E2E8F0; border-top-color: #F97316;
  border-radius: 50%; animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.footer-actions { display: flex; gap: 10px; margin-left: auto; }
.btn-cancel {
  padding: 9px 20px; border: 1px solid #E2E8F0;
  background: white; border-radius: 10px;
  font-weight: 700; color: #64748B; cursor: pointer;
  font-family: 'Mulish', sans-serif; font-size: 0.875rem;
  transition: 0.2s;
}
.btn-cancel:hover:not(:disabled) { background: #F1F5F9; }

.btn-draft {
  padding: 9px 18px; border: 1px solid #CBD5E1;
  background: white; border-radius: 10px;
  font-weight: 700; color: #475569; cursor: pointer;
  font-family: 'Mulish', sans-serif; font-size: 0.875rem;
  display: flex; align-items: center; gap: 7px; transition: 0.2s;
}
.btn-draft:hover:not(:disabled) { background: #F8FAFC; border-color: #94A3B8; }

.btn-send {
  padding: 9px 20px;
  background: linear-gradient(135deg, #F97316, #EA580C);
  border: none; border-radius: 10px;
  color: white; font-weight: 800; cursor: pointer;
  font-family: 'Mulish', sans-serif; font-size: 0.875rem;
  display: flex; align-items: center; gap: 8px;
  box-shadow: 0 4px 12px rgba(249, 115, 22, 0.35);
  transition: 0.2s;
}
.btn-send:hover:not(:disabled) { transform: translateY(-2px); box-shadow: 0 6px 18px rgba(249, 115, 22, 0.45); }
.btn-cancel:disabled, .btn-draft:disabled, .btn-send:disabled { opacity: 0.55; cursor: not-allowed; transform: none; }

/* Transitions */
.modal-fade-enter-active, .modal-fade-leave-active { transition: all 0.25s; }
.modal-fade-enter-from, .modal-fade-leave-to { opacity: 0; }
.modal-fade-enter-from .compose-card, .modal-fade-leave-to .compose-card { transform: scale(0.92); }

.slide-down-enter-active, .slide-down-leave-active { transition: all 0.22s ease; }
.slide-down-enter-from, .slide-down-leave-to { opacity: 0; transform: translateY(-8px); }
</style>
