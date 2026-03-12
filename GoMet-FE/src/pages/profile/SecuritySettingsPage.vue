<template>
  <div class="security-center-page">
    <section class="center-hero">
      <div>
        <div class="hero-kicker">{{ t('profile.security_center_kicker') }}</div>
        <h1>{{ t('profile.security_center_title') }}</h1>
        <p>
          {{ t('profile.security_center_desc') }}
        </p>
      </div>

      <div class="hero-actions">
        <span class="live-pill" :class="`is-${streamStatus}`">{{ liveStatusLabel }}</span>
        <button type="button" class="ghost-btn" :disabled="pageLoading" @click="refreshSecurityCenter">
          {{ t('profile.security_center_refresh') }}
        </button>
      </div>
    </section>

    <section class="overview-grid">
      <article class="overview-card overview-card-hero">
        <div class="overview-label">RiskEngine BE</div>
        <div class="overview-row">
          <span class="risk-pill" :class="riskBadge.className">{{ riskBadge.label }}</span>
          <strong>{{ securityScore }}</strong>
        </div>
        <p>{{ riskBadge.tip }}</p>
      </article>

      <article class="overview-card">
        <div class="overview-label">{{ t('profile.security_center_open_sessions') }}</div>
        <strong>{{ sessions.length }}</strong>
        <p>{{ t('profile.security_center_sessions_attention', { count: suspiciousSessions.length }) }}</p>
      </article>

      <article class="overview-card">
        <div class="overview-label">{{ t('profile.security_center_trusted_devices') }}</div>
        <strong>{{ devices.length }}</strong>
        <p>{{ latestTrustedDevice?.deviceName || t('profile.security_center_no_trusted_baseline') }}</p>
      </article>

      <article class="overview-card">
        <div class="overview-label">{{ t('profile.security_center_sync_checkpoint') }}</div>
        <strong>{{ snapshotUpdatedLabel }}</strong>
        <p>{{ securityRisk?.latestIp || t('profile.security_center_no_recent_ip') }}</p>
      </article>
    </section>

    <section class="center-layout">
      <div class="center-main">
        <article class="center-card">
          <div class="card-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_risk_reasons_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_risk_reasons_desc') }}</p>
            </div>
          </div>

          <div v-if="riskReasons.length" class="reason-list">
            <div v-for="reason in riskReasons" :key="reason.key" class="reason-item" :class="`reason-${reason.tone}`">
              <span class="reason-icon">{{ reason.icon }}</span>
              <div>
                <strong>{{ reason.title }}</strong>
                <p>{{ reason.description }}</p>
              </div>
            </div>
          </div>

          <div v-else class="empty-box">{{ t('profile.security_center_no_risk_signals') }}</div>
        </article>

        <article class="center-card">
          <div class="card-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_priority_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_priority_desc') }}</p>
            </div>
          </div>

          <div v-if="priorityActions.length" class="priority-list">
            <button
              v-for="action in priorityActions"
              :key="action.key"
              type="button"
              class="priority-item"
              :class="`priority-${action.tone}`"
              @click="runPriorityAction(action.key)"
            >
              <div>
                <strong>{{ action.label }}</strong>
                <p>{{ action.note }}</p>
              </div>
              <span>→</span>
            </button>
          </div>

          <div v-else class="empty-box">{{ t('profile.security_center_no_priority_actions') }}</div>
        </article>

        <article class="center-card" id="security-events-section">
          <div class="card-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_timeline_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_timeline_desc') }}</p>
            </div>

            <div class="segment-control">
              <button type="button" :class="{ active: eventFilter === 'all' }" @click="eventFilter = 'all'">{{ t('profile.security_center_filter_all') }}</button>
              <button type="button" :class="{ active: eventFilter === 'high' }" @click="eventFilter = 'high'">{{ t('profile.security_center_filter_high') }}</button>
              <button type="button" :class="{ active: eventFilter === 'auth' }" @click="eventFilter = 'auth'">{{ t('profile.security_center_filter_auth') }}</button>
            </div>
          </div>

          <div v-if="eventFeedLoading && !filteredEvents.length" class="empty-box">{{ t('profile.security_center_timeline_loading') }}</div>

          <div v-else-if="filteredEvents.length" class="timeline-list">
            <div v-for="event in filteredEvents" :key="event.id" class="timeline-item" :class="[`timeline-${event.severity}`, { 'is-live': newestLiveEventId === event.id }]">
              <div class="timeline-marker">{{ event.icon }}</div>
              <div>
                <div class="timeline-head">
                  <strong>{{ event.title }}</strong>
                  <span>{{ event.time }}</span>
                </div>
                <p>{{ event.description }}</p>
                <small>IP: {{ event.ip }}</small>
                <div v-if="event.canConfirm || event.canReport" class="timeline-actions">
                  <button
                    v-if="event.canConfirm"
                    type="button"
                    class="ghost-btn timeline-action-btn"
                    :disabled="isEventActionLoading(event.id)"
                    @click="confirmRiskEvent(event)"
                  >
                    {{ isEventActionLoading(event.id, 'THIS_IS_ME') ? t('profile.security_center_confirm_me_loading') : t('profile.security_center_confirm_me') }}
                  </button>
                  <button
                    v-if="event.canReport"
                    type="button"
                    class="danger-outline-btn timeline-action-btn"
                    :disabled="isEventActionLoading(event.id)"
                    @click="openDangerModal('report-event', event)"
                  >
                    {{ isEventActionLoading(event.id, 'THIS_WASNT_ME') ? t('profile.security_center_report_not_me_loading') : t('profile.security_center_report_not_me') }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-box">{{ t('profile.security_center_timeline_empty') }}</div>
        </article>

        <article class="center-card" id="security-sessions-section">
          <div class="card-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_sessions_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_sessions_desc') }}</p>
            </div>
            <button type="button" class="danger-outline-btn" :disabled="!revokableSessions.length || pageLoading" @click="openDangerModal('revoke-all-sessions')">
              {{ t('profile.security_center_revoke_all_other_sessions') }}
            </button>
          </div>

          <div class="entity-toolbar">
            <input v-model.trim="sessionSearch" type="search" class="filter-input" :placeholder="t('profile.security_center_session_search')">
            <div class="toolbar-inline">
              <div class="segment-control compact-control">
                <button type="button" :class="{ active: sessionGroupFilter === 'all' }" @click="sessionGroupFilter = 'all'">{{ t('profile.security_center_filter_all') }}</button>
                <button type="button" :class="{ active: sessionGroupFilter === 'current' }" @click="sessionGroupFilter = 'current'">{{ t('profile.security_center_group_current') }}</button>
                <button type="button" :class="{ active: sessionGroupFilter === 'suspicious' }" @click="sessionGroupFilter = 'suspicious'">{{ t('profile.security_center_group_suspicious') }}</button>
                <button type="button" :class="{ active: sessionGroupFilter === 'other' }" @click="sessionGroupFilter = 'other'">{{ t('profile.security_center_group_other') }}</button>
              </div>
              <select v-model="sessionSort" class="filter-select">
                <option value="recent">{{ t('profile.security_center_sort_recent') }}</option>
                <option value="name">{{ t('profile.security_center_sort_name') }}</option>
                <option value="ip">{{ t('profile.security_center_sort_ip') }}</option>
              </select>
            </div>
          </div>

          <div v-if="groupedSessions.length" class="group-list">
            <div v-for="group in groupedSessions" :key="group.key" class="group-block">
              <div class="group-head">
                <strong>{{ group.label }}</strong>
                <span>{{ group.items.length }}</span>
              </div>

              <div class="entity-list">
                <div v-for="session in group.items" :key="session.id" class="entity-row">
                  <div class="entity-icon">💻</div>
                  <div class="entity-copy">
                    <div class="entity-title-row">
                      <strong>{{ session.deviceName || session.userAgent || t('profile.security_center_unknown_device') }}</strong>
                      <span v-if="session.current" class="entity-badge current">{{ t('profile.security_center_current') }}</span>
                      <span v-else-if="isSuspiciousSession(session)" class="entity-badge warn">{{ t('profile.security_center_group_suspicious') }}</span>
                    </div>
                    <p>{{ session.ip || t('profile.security_center_unknown_ip') }} · {{ formatTime(session.lastUsedAt || session.createdAt) }}</p>
                  </div>
                  <button v-if="!session.current" type="button" class="danger-outline-btn" @click="openDangerModal('revoke-session', session)">
                    {{ t('profile.security_center_revoke') }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-box">{{ t('profile.security_center_sessions_empty') }}</div>
        </article>

        <article class="center-card" id="security-devices-section">
          <div class="card-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_trusted_devices') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_devices_desc') }}</p>
            </div>
            <button type="button" class="danger-outline-btn" :disabled="!devices.length || pageLoading" @click="openDangerModal('revoke-all-devices')">
              {{ t('profile.security_center_revoke_all_trust') }}
            </button>
          </div>

          <div class="entity-toolbar">
            <input v-model.trim="deviceSearch" type="search" class="filter-input" :placeholder="t('profile.security_center_device_search')">
            <div class="toolbar-inline">
              <div class="segment-control compact-control">
                <button type="button" :class="{ active: deviceGroupFilter === 'all' }" @click="deviceGroupFilter = 'all'">{{ t('profile.security_center_filter_all') }}</button>
                <button type="button" :class="{ active: deviceGroupFilter === 'current' }" @click="deviceGroupFilter = 'current'">{{ t('profile.security_center_device_current') }}</button>
                <button type="button" :class="{ active: deviceGroupFilter === 'review' }" @click="deviceGroupFilter = 'review'">{{ t('profile.security_center_review_needed') }}</button>
                <button type="button" :class="{ active: deviceGroupFilter === 'trusted' }" @click="deviceGroupFilter = 'trusted'">{{ t('profile.security_center_group_trusted') }}</button>
              </div>
              <select v-model="deviceSort" class="filter-select">
                <option value="recent">{{ t('profile.security_center_sort_recent') }}</option>
                <option value="name">{{ t('profile.security_center_sort_name') }}</option>
                <option value="ip">{{ t('profile.security_center_sort_ip') }}</option>
              </select>
            </div>
          </div>

          <div v-if="groupedDevices.length" class="group-list">
            <div v-for="group in groupedDevices" :key="group.key" class="group-block">
              <div class="group-head">
                <strong>{{ group.label }}</strong>
                <span>{{ group.items.length }}</span>
              </div>

              <div class="entity-list">
                <div v-for="device in group.items" :key="device.id" class="entity-row">
                  <div class="entity-icon">📱</div>
                  <div class="entity-copy">
                    <div class="entity-title-row">
                      <strong>{{ device.deviceName || device.deviceId || t('profile.security_center_unnamed_device') }}</strong>
                      <span v-if="isCurrentDevice(device)" class="entity-badge current">{{ t('profile.security_center_device_current') }}</span>
                      <span v-else-if="isReviewDevice(device)" class="entity-badge warn">{{ t('profile.security_center_review_needed') }}</span>
                      <span v-else class="entity-badge safe">{{ t('profile.security_center_group_trusted') }}</span>
                    </div>
                    <p>{{ device.lastIp || t('profile.security_center_unknown_ip') }} · {{ formatTime(device.lastSeenAt || device.firstSeenAt) }}</p>
                  </div>
                  <button type="button" class="danger-outline-btn" @click="openDangerModal('revoke-device', device)">
                    {{ t('profile.security_center_remove_trust') }}
                  </button>
                </div>
              </div>
            </div>
          </div>

          <div v-else class="empty-box">{{ t('profile.security_center_devices_empty') }}</div>
        </article>
      </div>

      <aside class="center-side">
        <article class="center-card">
          <div class="card-head compact-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_mfa_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_mfa_desc') }}</p>
            </div>
            <span class="risk-pill" :class="mfaStatus.mfaEnabled ? 'risk-low' : 'risk-medium'">
              {{ mfaStatus.mfaEnabled ? t('profile.security_center_enabled') : t('profile.security_center_not_enabled') }}
            </span>
          </div>
          <p class="side-copy">
            <template v-if="mfaStatus.mfaEnabled">
              {{ t('profile.security_center_backup_codes_remaining') }} <strong>{{ mfaStatus.remainingBackupCodes }}</strong>. {{ t('profile.security_center_store_codes_safe') }}
            </template>
            <template v-else>
              {{ t('profile.security_center_enable_totp_note') }}
            </template>
          </p>
          <div class="side-actions">
            <button v-if="!mfaStatus.mfaEnabled" type="button" class="primary-btn" @click="showSetupMfa = true">{{ t('profile.security_center_enable_mfa') }}</button>
            <button v-else type="button" class="danger-outline-btn" @click="showDisableMfa = true">{{ t('profile.security_center_disable_mfa') }}</button>
          </div>
        </article>

        <article class="center-card">
          <div class="card-head compact-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_latest_details_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_latest_details_desc') }}</p>
            </div>
          </div>
          <div class="detail-lines">
            <div>
              <span>{{ t('profile.security_center_latest_ip') }}</span>
              <strong>{{ securityRisk?.latestIp || t('profile.security_center_no_data') }}</strong>
            </div>
            <div>
              <span>{{ t('profile.security_center_latest_trusted_device') }}</span>
              <strong>{{ latestTrustedDevice?.deviceName || t('profile.security_center_no_trusted_device') }}</strong>
            </div>
            <div>
              <span>{{ t('profile.security_center_latest_activity') }}</span>
              <strong>{{ formatTime(securityRisk?.latestActivityAt) }}</strong>
            </div>
          </div>
        </article>

        <article class="center-card">
          <div class="card-head compact-head">
            <div>
              <div class="card-title">{{ t('profile.security_center_change_password_title') }}</div>
              <p class="card-subtitle">{{ t('profile.security_center_change_password_desc') }}</p>
            </div>
          </div>

          <form @submit.prevent="submitChangePassword" class="pw-form">
            <div class="field-group">
              <label>{{ t('profile.security_center_current_password') }}</label>
              <input v-model="pwForm.currentPassword" type="password" :placeholder="t('profile.security_center_current_password_placeholder')" required>
            </div>
            <div class="field-group">
              <label>{{ t('profile.security_center_new_password') }}</label>
              <input v-model="pwForm.newPassword" type="password" minlength="12" :placeholder="t('profile.security_center_new_password_placeholder')" required>
            </div>
            <div class="field-group">
              <label>{{ t('profile.security_center_confirm_password') }}</label>
              <input v-model="pwForm.confirmPassword" type="password" :placeholder="t('profile.security_center_confirm_password_placeholder')" required>
            </div>
            <p v-if="pwError" class="msg-error">{{ pwError }}</p>
            <p v-if="pwSuccess" class="msg-success">{{ pwSuccess }}</p>
            <button type="submit" class="primary-btn" :disabled="changingPw">
              {{ changingPw ? t('profile.saving') : t('profile.security_center_update_password') }}
            </button>
          </form>
        </article>
      </aside>
    </section>

    <Teleport to="body">
      <div v-if="showSetupMfa" class="overlay" @click.self="closeMfaSetup">
        <div class="overlay-box overlay-lg" @click.stop>
          <button @click="closeMfaSetup" class="overlay-close">×</button>
          <TwoFactorSetup @done="onMfaEnabled" />
        </div>
      </div>

      <div v-if="showDisableMfa" class="overlay" @click.self="showDisableMfa = false">
        <div class="overlay-box overlay-sm" @click.stop>
          <h3 class="modal-title">{{ t('profile.security_center_disable_mfa_modal_title') }}</h3>
          <p class="modal-desc">{{ t('profile.security_center_disable_mfa_modal_desc') }}</p>
          <input v-model="disableMfaCode" type="text" inputmode="numeric" maxlength="8" :placeholder="t('profile.security_center_verification_code')" class="modal-input">
          <p v-if="disableMfaError" class="msg-error">{{ disableMfaError }}</p>
          <div class="modal-actions">
            <button type="button" class="ghost-btn" @click="showDisableMfa = false">{{ t('common.cancel') }}</button>
            <button type="button" class="danger-solid-btn" :disabled="!disableMfaCode" @click="submitDisableMfa">{{ t('profile.security_center_disable_mfa') }}</button>
          </div>
        </div>
      </div>
    </Teleport>

    <ConfirmDangerModal
      v-model="dangerModal.open"
      :eyebrow="t('profile.security_center_sensitive_action')"
      :title="dangerModal.title"
      :description="dangerModal.description"
      :highlights="dangerModal.highlights"
      :confirm-label="dangerModal.confirmLabel"
      :loading="dangerModal.loading"
      @cancel="closeDangerModal"
      @confirm="confirmDangerModal"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import * as authService from '@/services/authService'
import TwoFactorSetup from '@/components/modals/TwoFactorSetup.vue'
import ConfirmDangerModal from '@/components/common/ConfirmDangerModal.vue'
import { parseSecurityEventMeta, useSecurityMonitor } from '@/composables/useSecurityMonitor'
import { toast } from '@/composables/useToast'

const { t, locale } = useI18n()

const eventFilter = ref('all')
const sessionSearch = ref('')
const sessionSort = ref('recent')
const sessionGroupFilter = ref('all')
const deviceSearch = ref('')
const deviceSort = ref('recent')
const deviceGroupFilter = ref('all')
const eventActionLoading = ref({})

const {
  sessions,
  devices,
  securityEvents,
  securityRisk,
  securityFeedLoading: eventFeedLoading,
  snapshotSyncing: pageLoading,
  snapshotUpdatedAt,
  streamStatus,
  newestLiveEventId,
  mfaStatus,
  suspiciousSessions,
  revokableSessions,
  refreshSecurityState,
  startLiveStream,
  stopLiveStream,
  revokeSession,
  revokeAllSessions,
  revokeDevice,
  revokeAllDevices,
  sendEventFeedback
} = useSecurityMonitor({ eventPageSize: 20 })

const showSetupMfa = ref(false)
const showDisableMfa = ref(false)
const disableMfaCode = ref('')
const disableMfaError = ref('')

const pwForm = reactive({ currentPassword: '', newPassword: '', confirmPassword: '' })
const pwError = ref('')
const pwSuccess = ref('')
const changingPw = ref(false)

const dangerModal = ref({ open: false, action: '', payload: null, title: '', description: '', highlights: [], confirmLabel: '', loading: false })

const currentLocale = computed(() => (locale.value === 'vi' ? 'vi-VN' : 'en-US'))

const riskBadge = computed(() => {
  if (securityRisk.value?.riskLevel === 'HIGH') {
    return { label: t('profile.risk_high'), className: 'risk-high', tip: t('profile.risk_high_tip') }
  }
  if (securityRisk.value?.riskLevel === 'LOW') {
    return { label: t('profile.risk_low'), className: 'risk-low', tip: t('profile.security_center_risk_low_tip') }
  }
  return { label: t('profile.risk_medium'), className: 'risk-medium', tip: t('profile.security_center_risk_medium_tip') }
})

const securityScore = computed(() => {
  let score = 40
  if (mfaStatus.mfaEnabled) score += 24
  if ((securityRisk.value?.trustedDeviceCount ?? devices.value.length) > 0) score += 18
  if ((securityRisk.value?.activeSessionCount ?? sessions.value.length) <= 2 && (securityRisk.value?.activeSessionCount ?? sessions.value.length) > 0) score += 10
  if (securityRisk.value?.deviceTrusted) score += 10
  if (!securityRisk.value?.ipChanged && securityRisk.value?.latestIp) score += 8
  if (securityRisk.value?.riskLevel === 'HIGH') score -= 20
  if (securityRisk.value?.riskLevel === 'MEDIUM') score -= 6
  if (!mfaStatus.mfaEnabled) score -= 12
  return Math.max(0, Math.min(100, score))
})

const liveStatusLabel = computed(() => {
  if (pageLoading.value) return t('profile.security_center_syncing')
  if (streamStatus.value === 'live') return t('profile.security_center_live')
  if (streamStatus.value === 'reconnecting') return t('profile.security_center_reconnecting')
  return t('profile.security_center_paused')
})

const snapshotUpdatedLabel = computed(() => {
  if (!snapshotUpdatedAt.value) return t('profile.security_center_not_available')
  return formatTime(snapshotUpdatedAt.value)
})

const latestTrustedDevice = computed(() => {
  return [...devices.value].sort((left, right) => {
    const a = new Date(left.lastSeenAt || left.firstSeenAt || 0).getTime()
    const b = new Date(right.lastSeenAt || right.firstSeenAt || 0).getTime()
    return b - a
  })[0] || null
})

const currentSessionDeviceIds = computed(() => new Set(
  sessions.value
    .filter(session => session.current && session.deviceId)
    .map(session => session.deviceId)
))

const riskReasons = computed(() => (securityRisk.value?.reasons || []).map(mapRiskReason).filter(Boolean))

const priorityActions = computed(() => {
  return (securityRisk.value?.recommendedActions || [])
    .map(mapPriorityAction)
    .filter(Boolean)
    .filter((item, index, all) => all.findIndex(candidate => candidate.key === item.key) === index)
    .slice(0, 4)
})

const filteredEvents = computed(() => {
  const mapped = securityEvents.value.map(mapSecurityEvent)
  if (eventFilter.value === 'high') {
    return mapped.filter(event => event.severity === 'high')
  }
  if (eventFilter.value === 'auth') {
    return mapped.filter(event => ['LOGIN_SUCCESS', 'LOGIN_SUSPICIOUS', 'MFA_ENABLED', 'MFA_DISABLED', 'MFA_BACKUP_CODE_USED', 'PASSWORD_CHANGE'].includes(event.rawType))
  }
  return mapped
})

const groupedSessions = computed(() => {
  const query = sessionSearch.value.trim().toLowerCase()
  const sorted = [...sessions.value]
    .filter(session => {
      if (!query) return true
      const haystack = [session.deviceName, session.userAgent, session.ip, session.deviceId]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
      return haystack.includes(query)
    })
    .sort((left, right) => compareSessions(left, right, sessionSort.value))

  const groups = [
    { key: 'current', label: t('profile.security_center_group_current_device'), items: sorted.filter(session => session.current) },
    { key: 'suspicious', label: t('profile.security_center_group_suspicious_sessions'), items: sorted.filter(session => !session.current && isSuspiciousSession(session)) },
    { key: 'other', label: t('profile.security_center_group_other_sessions'), items: sorted.filter(session => !session.current && !isSuspiciousSession(session)) }
  ]

  return groups.filter(group => (sessionGroupFilter.value === 'all' || sessionGroupFilter.value === group.key) && group.items.length)
})

const groupedDevices = computed(() => {
  const query = deviceSearch.value.trim().toLowerCase()
  const sorted = [...devices.value]
    .filter(device => {
      if (!query) return true
      const haystack = [device.deviceName, device.deviceId, device.lastIp]
        .filter(Boolean)
        .join(' ')
        .toLowerCase()
      return haystack.includes(query)
    })
    .sort((left, right) => compareDevices(left, right, deviceSort.value))

  const groups = [
    { key: 'current', label: t('profile.security_center_group_current_device'), items: sorted.filter(isCurrentDevice) },
    { key: 'review', label: t('profile.security_center_group_review_devices'), items: sorted.filter(device => !isCurrentDevice(device) && isReviewDevice(device)) },
    { key: 'trusted', label: t('profile.security_center_group_trusted_baseline'), items: sorted.filter(device => !isCurrentDevice(device) && !isReviewDevice(device)) }
  ]

  return groups.filter(group => (deviceGroupFilter.value === 'all' || deviceGroupFilter.value === group.key) && group.items.length)
})

function formatTime(iso) {
  if (!iso) return t('profile.security_center_time_unknown')
  const date = new Date(iso)
  if (Number.isNaN(date.getTime())) return t('profile.security_center_time_unknown')
  return date.toLocaleString(currentLocale.value)
}

function isSuspiciousSession(session) {
  if (session.current) return false
  const trusted = Boolean(session.deviceId && devices.value.some(device => device.deviceId === session.deviceId))
  const ipChanged = Boolean(securityRisk.value?.latestIp && session.ip && session.ip !== securityRisk.value.latestIp)
  return !trusted || ipChanged
}

function isCurrentDevice(device) {
  return Boolean(device.deviceId && currentSessionDeviceIds.value.has(device.deviceId))
}

function isReviewDevice(device) {
  return Boolean(securityRisk.value?.latestIp && device.lastIp && device.lastIp !== securityRisk.value.latestIp)
}

function compareSessions(left, right, sortKey) {
  if (sortKey === 'name') {
    return (left.deviceName || left.userAgent || '').localeCompare(right.deviceName || right.userAgent || '', locale.value)
  }
  if (sortKey === 'ip') {
    return (left.ip || '').localeCompare(right.ip || '', locale.value)
  }
  return new Date(right.lastUsedAt || right.createdAt || 0).getTime() - new Date(left.lastUsedAt || left.createdAt || 0).getTime()
}

function compareDevices(left, right, sortKey) {
  if (sortKey === 'name') {
    return (left.deviceName || left.deviceId || '').localeCompare(right.deviceName || right.deviceId || '', locale.value)
  }
  if (sortKey === 'ip') {
    return (left.lastIp || '').localeCompare(right.lastIp || '', locale.value)
  }
  return new Date(right.lastSeenAt || right.firstSeenAt || 0).getTime() - new Date(left.lastSeenAt || left.firstSeenAt || 0).getTime()
}

function mapSecurityEvent(event) {
  const meta = parseSecurityEventMeta(event.eventMetaJson)
  const base = {
    id: event.id,
    rawType: event.eventType,
    meta,
    time: formatTime(event.createdAt),
    ip: event.ip || t('profile.security_center_time_unknown'),
    severity: 'low',
    icon: '•',
    title: event.eventType,
    description: t('profile.event_generic_desc'),
    canConfirm: false,
    canReport: false
  }

  switch (event.eventType) {
    case 'LOGIN_SUSPICIOUS':
      return {
        ...base,
        severity: 'high',
        icon: '!',
        title: t('profile.event_login_suspicious_title'),
        description: t('profile.event_login_suspicious_desc'),
        canConfirm: Boolean(meta.deviceId),
        canReport: true
      }
    case 'LOGIN_SUCCESS':
      return { ...base, severity: 'low', icon: '→', title: t('profile.event_login_success_title'), description: t('profile.event_login_success_desc') }
    case 'SESSION_REVOKE':
      return { ...base, severity: 'medium', icon: '↗', title: t('profile.event_session_revoked_title'), description: t('profile.event_session_revoked_desc', { id: meta.sessionId || '-' }) }
    case 'SESSION_REVOKE_ALL':
      return { ...base, severity: 'medium', icon: '↗', title: t('profile.event_session_revoke_all_title'), description: t('profile.event_session_revoke_all_desc') }
    case 'DEVICE_REVOKED':
      return { ...base, severity: 'medium', icon: '✕', title: t('profile.event_device_revoked_title'), description: t('profile.event_device_revoked_desc', { id: meta.deviceDbId || '-' }) }
    case 'DEVICE_REVOKE_ALL':
      return { ...base, severity: 'medium', icon: '✕', title: t('profile.event_device_revoke_all_title'), description: t('profile.event_device_revoke_all_desc') }
    case 'DEVICE_TRUSTED':
      return { ...base, severity: 'low', icon: '✓', title: t('profile.event_device_trusted_title'), description: t('profile.event_device_trusted_desc') }
    case 'MFA_ENABLED':
      return { ...base, severity: 'low', icon: '🛡', title: t('profile.event_mfa_enabled_title'), description: t('profile.event_mfa_enabled_desc') }
    case 'MFA_DISABLED':
      return { ...base, severity: 'high', icon: '⚠', title: t('profile.event_mfa_disabled_title'), description: t('profile.event_mfa_disabled_desc') }
    case 'PASSWORD_CHANGE':
      return { ...base, severity: 'low', icon: '🔑', title: t('profile.event_password_change_title'), description: t('profile.event_password_change_desc') }
    case 'REFRESH_TOKEN_REUSE_DETECTED':
      return { ...base, severity: 'high', icon: '⚠', title: t('profile.event_refresh_reuse_title'), description: t('profile.event_refresh_reuse_desc'), canReport: true }
    case 'THIS_WASNT_ME':
      return { ...base, severity: 'high', icon: '✕', title: t('profile.security_center_report_not_me_done_title'), description: t('profile.security_center_report_not_me_done_desc') }
    default:
      return base
  }
}

function mapRiskReason(reasonKey) {
  switch (reasonKey) {
    case 'trusted_device':
      return { key: reasonKey, tone: 'good', icon: '✓', title: t('profile.risk_reason_trusted_device_title'), description: t('profile.risk_reason_trusted_device_desc') }
    case 'no_device_history':
      return { key: reasonKey, tone: 'review', icon: '◎', title: t('profile.risk_reason_no_device_history_title'), description: t('profile.risk_reason_no_device_history_desc') }
    case 'same_device_as_last':
      return { key: reasonKey, tone: 'good', icon: '↺', title: t('profile.risk_reason_same_device_as_last_title'), description: t('profile.risk_reason_same_device_as_last_desc') }
    case 'ip_changed':
      return { key: reasonKey, tone: 'critical', icon: '!', title: t('profile.risk_reason_ip_changed_title'), description: t('profile.risk_reason_ip_changed_desc') }
    case 'missing_device_fingerprint':
      return { key: reasonKey, tone: 'review', icon: '◌', title: t('profile.risk_reason_missing_device_fingerprint_title'), description: t('profile.risk_reason_missing_device_fingerprint_desc') }
    case 'no_trusted_devices':
      return { key: reasonKey, tone: 'review', icon: '•', title: t('profile.risk_reason_no_trusted_devices_title'), description: t('profile.risk_reason_no_trusted_devices_desc') }
    case 'many_active_sessions':
      return { key: reasonKey, tone: 'review', icon: '↗', title: t('profile.risk_reason_many_active_sessions_title'), description: t('profile.risk_reason_many_active_sessions_desc', { count: securityRisk.value?.activeSessionCount ?? sessions.value.length }) }
    case 'mfa_disabled':
      return { key: reasonKey, tone: 'critical', icon: '🔓', title: t('profile.risk_reason_mfa_disabled_title'), description: t('profile.risk_reason_mfa_disabled_desc') }
    case 'verification_required':
      return { key: reasonKey, tone: 'critical', icon: '⚠', title: t('profile.risk_reason_verification_required_title'), description: t('profile.risk_reason_verification_required_desc') }
    default:
      return null
  }
}

function mapPriorityAction(actionKey) {
  switch (actionKey) {
    case 'enable_mfa':
      return { key: actionKey, tone: 'primary', label: t('profile.priority_enable_mfa_label'), note: t('profile.priority_enable_mfa_note') }
    case 'review_latest_device':
      return { key: actionKey, tone: 'warning', label: t('profile.priority_review_latest_device_label'), note: t('profile.priority_review_latest_device_note') }
    case 'review_recent_ip':
      return { key: actionKey, tone: 'warning', label: t('profile.priority_review_recent_ip_label'), note: t('profile.priority_review_recent_ip_note') }
    case 'review_sessions':
      return { key: actionKey, tone: 'danger', label: t('profile.priority_review_sessions_label'), note: t('profile.priority_review_sessions_note', { count: suspiciousSessions.value.length || sessions.value.length }) }
    case 'add_trusted_device':
      return { key: actionKey, tone: 'primary', label: t('profile.priority_add_trusted_device_label'), note: t('profile.priority_add_trusted_device_note') }
    default:
      return null
  }
}

async function refreshSecurityCenter() {
  try {
    await refreshSecurityState()
  } catch {
    toast.error(t('profile.security_center_refresh_failed'))
  }
}

function openDangerModal(action, payload = null) {
  const config = {
    'revoke-session': {
      title: t('profile.security_center_revoke_session_title'),
      description: t('profile.security_center_revoke_session_desc'),
      highlights: [payload?.deviceName || payload?.userAgent || payload?.ip || t('profile.security_center_unknown_session')],
      confirmLabel: t('profile.security_center_revoke_session_confirm')
    },
    'revoke-all-sessions': {
      title: t('profile.security_center_revoke_all_sessions_title'),
      description: t('profile.security_center_revoke_all_sessions_desc'),
      highlights: revokableSessions.value.slice(0, 3).map(session => session.deviceName || session.userAgent || session.ip || t('profile.security_center_unknown_session')),
      confirmLabel: t('profile.security_center_revoke_all_sessions_confirm')
    },
    'revoke-device': {
      title: t('profile.security_center_revoke_device_title'),
      description: t('profile.security_center_revoke_device_desc'),
      highlights: [payload?.deviceName || payload?.deviceId || t('profile.security_center_unknown_device')],
      confirmLabel: t('profile.security_center_revoke_device_confirm')
    },
    'revoke-all-devices': {
      title: t('profile.security_center_revoke_all_devices_title'),
      description: t('profile.security_center_revoke_all_devices_desc'),
      highlights: devices.value.slice(0, 3).map(device => device.deviceName || device.deviceId || t('profile.security_center_unknown_device')),
      confirmLabel: t('profile.security_center_revoke_all_devices_confirm')
    },
    'report-event': {
      title: t('profile.security_center_report_event_title'),
      description: t('profile.security_center_report_event_desc'),
      highlights: [payload?.title || t('profile.security_center_high_risk_event'), payload?.ip ? `IP ${payload.ip}` : null].filter(Boolean),
      confirmLabel: t('profile.security_center_report_not_me')
    }
  }[action]

  if (!config) return

  dangerModal.value = { open: true, action, payload, loading: false, ...config }
}

function closeDangerModal() {
  dangerModal.value = { open: false, action: '', payload: null, title: '', description: '', highlights: [], confirmLabel: '', loading: false }
}

async function confirmDangerModal() {
  if (!dangerModal.value.action) return

  dangerModal.value = { ...dangerModal.value, loading: true }
  try {
    if (dangerModal.value.action === 'revoke-session' && dangerModal.value.payload?.id) {
      await revokeSession(dangerModal.value.payload.id)
      toast.success(t('profile.security_center_revoke_session_success'))
    }

    if (dangerModal.value.action === 'revoke-all-sessions') {
      await revokeAllSessions()
      toast.success(t('profile.security_center_revoke_all_sessions_success'))
    }

    if (dangerModal.value.action === 'revoke-device' && dangerModal.value.payload?.id) {
      await revokeDevice(dangerModal.value.payload.id)
      toast.success(t('profile.security_center_revoke_device_success'))
    }

    if (dangerModal.value.action === 'revoke-all-devices') {
      await revokeAllDevices()
      toast.success(t('profile.security_center_revoke_all_devices_success'))
    }

    if (dangerModal.value.action === 'report-event' && dangerModal.value.payload?.id) {
      await submitEventFeedback(dangerModal.value.payload.id, 'THIS_WASNT_ME')
      toast.success(t('profile.security_center_report_event_success'))
    }

    closeDangerModal()
  } catch {
    toast.error(t('profile.security_center_action_failed'))
    dangerModal.value = { ...dangerModal.value, loading: false }
  }
}

function runPriorityAction(actionKey) {
  if (actionKey === 'enable_mfa') {
    showSetupMfa.value = true
    return
  }

  if (actionKey === 'review_latest_device') {
    if (latestTrustedDevice.value) {
      openDangerModal('revoke-device', latestTrustedDevice.value)
    } else {
      document.getElementById('security-devices-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
    return
  }

  if (actionKey === 'review_sessions') {
    if (suspiciousSessions.value.length) {
      openDangerModal('revoke-all-sessions')
    } else {
      document.getElementById('security-sessions-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    }
    return
  }

  if (actionKey === 'review_recent_ip') {
    document.getElementById('security-events-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
    return
  }

  if (actionKey === 'add_trusted_device') {
    document.getElementById('security-devices-section')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

function isEventActionLoading(eventId, action = null) {
  const current = eventActionLoading.value[String(eventId)]
  if (!current) return false
  return action ? current === action : true
}

async function submitEventFeedback(eventId, action) {
  eventActionLoading.value = { ...eventActionLoading.value, [String(eventId)]: action }
  try {
    return await sendEventFeedback(eventId, action)
  } finally {
    const next = { ...eventActionLoading.value }
    delete next[String(eventId)]
    eventActionLoading.value = next
  }
}

async function confirmRiskEvent(event) {
  try {
    await submitEventFeedback(event.id, 'THIS_IS_ME')
    toast.success(t('profile.security_center_confirm_me_success'))
  } catch {
    toast.error(t('profile.security_center_confirm_me_failed'))
  }
}

async function closeMfaSetup() {
  showSetupMfa.value = false
  await refreshSecurityCenter()
}

async function onMfaEnabled() {
  showSetupMfa.value = false
  await refreshSecurityCenter()
  toast.success(t('profile.event_mfa_enabled_desc'))
}

async function submitDisableMfa() {
  disableMfaError.value = ''
  const code = disableMfaCode.value.trim()
  try {
    await authService.disableMfa(code.length > 6 ? { backupCode: code } : { totpCode: code })
    showDisableMfa.value = false
    disableMfaCode.value = ''
    mfaStatus.mfaEnabled = false
    mfaStatus.remainingBackupCodes = 0
    toast.success(t('profile.event_mfa_disabled_desc'))
  } catch (error) {
    disableMfaError.value = error.response?.data?.message || t('profile.security_center_invalid_code')
  }
}

async function submitChangePassword() {
  pwError.value = ''
  pwSuccess.value = ''
  if (pwForm.newPassword !== pwForm.confirmPassword) {
    pwError.value = t('auth.error_pw_match')
    return
  }

  changingPw.value = true
  try {
    await authService.changePassword(pwForm.currentPassword, pwForm.newPassword)
    Object.assign(pwForm, { currentPassword: '', newPassword: '', confirmPassword: '' })
    pwSuccess.value = t('profile.event_password_change_desc')
  } catch (error) {
    pwError.value = error.response?.data?.message || t('profile.security_center_change_password_failed')
  } finally {
    changingPw.value = false
  }
}

onMounted(async () => {
  await refreshSecurityCenter()
  startLiveStream()
})

onUnmounted(() => {
  stopLiveStream()
})
</script>

<style scoped lang="scss">
@use '@/assets/styles/variables' as *;

.security-center-page {
  max-width: 1240px;
  margin: 0 auto;
  padding: 28px 20px 64px;
  display: grid;
  gap: 18px;
}

.center-hero {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 18px;
  padding: 24px;
  border-radius: 28px;
  background:
    radial-gradient(circle at top left, rgba(251, 191, 36, 0.18), transparent 26%),
    radial-gradient(circle at bottom right, rgba(59, 130, 246, 0.16), transparent 28%),
    linear-gradient(135deg, #fff7ed, #ffffff 58%, #eff6ff);
  border: 1px solid rgba(253, 186, 116, 0.34);

  h1 {
    margin: 6px 0 8px;
    font-size: 2rem;
    color: #0f172a;
  }

  p {
    margin: 0;
    max-width: 680px;
    color: #475569;
    line-height: 1.65;
  }
}

.hero-kicker {
  display: inline-flex;
  padding: 5px 10px;
  border-radius: 999px;
  background: #ffffff;
  border: 1px solid #fdba74;
  color: #c2410c;
  font-size: 11px;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.live-pill,
.risk-pill,
.entity-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 800;
  border: 1px solid transparent;
}

.live-pill.is-live { background: #dcfce7; color: #166534; border-color: #86efac; }
.live-pill.is-reconnecting { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.live-pill.is-idle { background: #e2e8f0; color: #475569; border-color: #cbd5e1; }

.risk-low { background: #dcfce7; color: #166534; border-color: #86efac; }
.risk-medium { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.risk-high { background: #ffe4e6; color: #be123c; border-color: #fda4af; }

.ghost-btn,
.primary-btn,
.danger-solid-btn,
.danger-outline-btn {
  border-radius: 12px;
  padding: 10px 14px;
  font-weight: 700;
  cursor: pointer;
  border: none;
}

.ghost-btn {
  background: #fff;
  border: 1px solid #cbd5e1;
  color: #334155;
}

.primary-btn {
  background: linear-gradient(135deg, #2563eb, #1d4ed8);
  color: #fff;
}

.danger-solid-btn {
  background: linear-gradient(135deg, #ef4444, #b91c1c);
  color: #fff;
}

.danger-outline-btn {
  background: #fff;
  color: #dc2626;
  border: 1px solid #fca5a5;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
}

.overview-card {
  padding: 18px;
  border-radius: 22px;
  background: #fff;
  border: 1px solid #e2e8f0;

  strong {
    display: block;
    margin-top: 4px;
    color: #0f172a;
    font-size: 28px;
  }

  p {
    margin: 8px 0 0;
    color: #64748b;
    line-height: 1.5;
  }
}

.overview-card-hero {
  background: linear-gradient(135deg, #0f172a, #1e3a8a);
  border-color: #1d4ed8;

  .overview-label,
  p,
  strong {
    color: #fff;
  }
}

.overview-label {
  font-size: 12px;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: #64748b;
}

.overview-row {
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.center-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(320px, 0.9fr);
  gap: 18px;
}

.center-main,
.center-side {
  display: grid;
  gap: 18px;
  align-content: start;
}

.center-card {
  padding: 20px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid #e2e8f0;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.04);
}

.card-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  margin-bottom: 16px;
}

.compact-head { margin-bottom: 12px; }

.card-title {
  color: #0f172a;
  font-size: 18px;
  font-weight: 800;
}

.card-subtitle {
  margin: 4px 0 0;
  color: #64748b;
  font-size: 14px;
  line-height: 1.55;
}

.reason-list,
.priority-list,
.entity-list,
.timeline-list {
  display: grid;
  gap: 12px;
}

.reason-item,
.priority-item,
.entity-row,
.timeline-item,
.empty-box {
  border-radius: 18px;
  border: 1px solid #e2e8f0;
}

.reason-item,
.timeline-item {
  display: grid;
  grid-template-columns: 34px 1fr;
  gap: 12px;
  padding: 14px;

  strong { color: #0f172a; }
  p { margin: 4px 0 0; color: #475569; line-height: 1.5; }
}

.reason-good { background: #f0fdf4; border-color: #bbf7d0; }
.reason-review { background: #fffbeb; border-color: #fde68a; }
.reason-critical { background: #fff1f2; border-color: #fecaca; }

.reason-icon,
.timeline-marker,
.entity-icon {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.82);
  font-weight: 800;
}

.priority-item {
  width: 100%;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 14px;
  text-align: left;
  background: #fff;
  cursor: pointer;

  strong { color: #0f172a; display: block; }
  p { margin: 4px 0 0; color: #475569; }
  span { color: #64748b; font-size: 18px; font-weight: 800; }
}

.priority-primary { background: linear-gradient(135deg, #eff6ff, #fff); border-color: #bfdbfe; }
.priority-warning { background: linear-gradient(135deg, #fff7ed, #fff); border-color: #fdba74; }
.priority-danger { background: linear-gradient(135deg, #fff1f2, #fff); border-color: #fda4af; }

.entity-toolbar,
.toolbar-inline,
.group-list,
.group-block,
.timeline-actions {
  display: grid;
  gap: 12px;
}

.entity-toolbar {
  margin-bottom: 14px;
}

.toolbar-inline {
  grid-template-columns: minmax(0, 1fr) 180px;
  align-items: center;
}

.compact-control {
  width: 100%;
  overflow-x: auto;
}

.filter-input,
.filter-select {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  padding: 11px 13px;
  background: #fff;
  color: #0f172a;
}

.group-block {
  gap: 10px;
}

.group-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  color: #334155;

  span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 28px;
    height: 28px;
    padding: 0 10px;
    border-radius: 999px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    color: #64748b;
    font-size: 12px;
    font-weight: 800;
  }
}

.segment-control {
  display: inline-flex;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  padding: 4px;

  button {
    border: none;
    background: transparent;
    padding: 8px 12px;
    border-radius: 10px;
    color: #64748b;
    font-weight: 700;
    cursor: pointer;
  }

  .active {
    background: #fff;
    color: #0f172a;
    box-shadow: 0 1px 2px rgba(15, 23, 42, 0.08);
  }
}

.timeline-low { background: #f0fdf4; border-color: #bbf7d0; }
.timeline-medium { background: #fffbeb; border-color: #fde68a; }
.timeline-high { background: #fff1f2; border-color: #fecaca; }
.timeline-item.is-live { box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.15); }

.timeline-actions {
  margin-top: 10px;
  grid-template-columns: repeat(auto-fit, minmax(140px, max-content));
}

.timeline-action-btn {
  width: auto;
}

.timeline-head,
.entity-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  flex-wrap: wrap;
}

.timeline-head span,
.timeline-item small,
.entity-copy p,
.side-copy,
.detail-lines span {
  color: #64748b;
}

.entity-row {
  display: grid;
  grid-template-columns: 34px 1fr auto;
  gap: 12px;
  align-items: center;
  padding: 14px;
  background: #fff;
}

.entity-copy strong { color: #0f172a; }
.entity-copy p { margin: 4px 0 0; }
.entity-badge.current { background: #dbeafe; color: #1d4ed8; border-color: #93c5fd; }
.entity-badge.warn { background: #fff7ed; color: #c2410c; border-color: #fdba74; }
.entity-badge.safe { background: #dcfce7; color: #166534; border-color: #86efac; }

.empty-box {
  padding: 18px;
  background: #f8fafc;
  color: #64748b;
  line-height: 1.6;
}

.side-copy { line-height: 1.6; margin: 0; }
.side-actions { margin-top: 14px; display: flex; gap: 10px; }

.detail-lines {
  display: grid;
  gap: 12px;

  div {
    display: grid;
    gap: 4px;
    padding: 12px 14px;
    border-radius: 16px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
  }

  strong { color: #0f172a; }
}

.pw-form {
  display: grid;
  gap: 12px;
}

.field-group {
  display: grid;
  gap: 6px;

  label { font-size: 13px; font-weight: 700; color: #334155; }
  input {
    border: 1px solid #cbd5e1;
    border-radius: 12px;
    padding: 11px 13px;
    outline: none;
    font-size: 14px;
    color: #0f172a;
    transition: border-color 0.18s ease, box-shadow 0.18s ease;

    &:focus {
      border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
    }
  }
}

.msg-error { color: #dc2626; margin: 0; }
.msg-success { color: #16a34a; margin: 0; }

.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  z-index: 1100;
}

.overlay-box {
  width: 100%;
  background: #fff;
  border-radius: 24px;
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 24px 80px rgba(15, 23, 42, 0.28);
}

.overlay-lg { max-width: 560px; }
.overlay-sm { max-width: 420px; padding: 24px; }

.overlay-close {
  position: absolute;
  top: 14px;
  right: 14px;
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: none;
  background: #f1f5f9;
  color: #64748b;
  cursor: pointer;
  font-size: 22px;
}

.modal-title { margin: 0 0 8px; color: #0f172a; }
.modal-desc { margin: 0 0 12px; color: #64748b; line-height: 1.6; }
.modal-input {
  width: 100%;
  box-sizing: border-box;
  border: 1px solid #cbd5e1;
  border-radius: 12px;
  padding: 11px 13px;
}
.modal-actions { margin-top: 14px; display: flex; justify-content: flex-end; gap: 10px; }

@media (max-width: 1080px) {
  .overview-grid,
  .center-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .security-center-page { padding: 18px 12px 40px; }
  .center-hero,
  .card-head,
  .hero-actions {
    flex-direction: column;
    align-items: flex-start;
  }

  .overview-grid { grid-template-columns: 1fr; }
  .toolbar-inline { grid-template-columns: 1fr; }
  .segment-control,
  .side-actions,
  .modal-actions { width: 100%; }
  .segment-control { display: grid; grid-template-columns: repeat(3, 1fr); }
  .ghost-btn,
  .primary-btn,
  .danger-solid-btn,
  .danger-outline-btn { width: 100%; justify-content: center; }
  .entity-row { grid-template-columns: 1fr; }
}
</style>
