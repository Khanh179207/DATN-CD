<template>
  <span :class="['status-badge', `status-badge--${normalized}`]">
    {{ label }}
  </span>
</template>

<script setup>
const props = defineProps({
  /** PostStatus enum value: PENDING_REVIEW | APPROVED | REJECTED | HIDDEN */
  status: { type: String, required: true }
})

const MAP = {
  PENDING_REVIEW: { label: 'Pending Review', key: 'pending' },
  APPROVED:       { label: 'Approved',        key: 'approved' },
  REJECTED:       { label: 'Rejected',        key: 'rejected' },
  HIDDEN:         { label: 'Hidden',          key: 'hidden' },
}

const entry     = computed(() => MAP[props.status] ?? { label: props.status, key: 'unknown' })
const label     = computed(() => entry.value.label)
const normalized = computed(() => entry.value.key)

import { computed } from 'vue'
</script>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 999px;
  font-size: 0.72rem;
  font-weight: 600;
  letter-spacing: 0.03em;
  text-transform: uppercase;
  white-space: nowrap;
}

.status-badge--pending  { background: #fef3c7; color: #92400e; }
.status-badge--approved { background: #dcfce7; color: #166534; }
.status-badge--rejected { background: #fee2e2; color: #991b1b; }
.status-badge--hidden   { background: #e5e7eb; color: #374151; }
.status-badge--unknown  { background: #f3f4f6; color: #6b7280; }
</style>
