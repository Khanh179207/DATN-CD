<template>
  <div class="lang-pill" role="group" :aria-label="t('common.switch_language')">
    <div class="pill-track">
      <!-- Sliding thumb -->
      <div
        class="pill-thumb"
        :style="{ left: locale === 'vi' ? '3px' : '50%' }"
        aria-hidden="true"
      ></div>
      <!-- Language buttons -->
      <button
        v-for="opt in options"
        :key="opt.code"
        class="pill-btn"
        :class="{ active: locale === opt.code }"
        :aria-pressed="locale === opt.code"
        @click="select(opt.code)"
      >
        <span class="pill-flag">{{ opt.flag }}</span>
        <span class="pill-code">{{ opt.code.toUpperCase() }}</span>
      </button>
    </div>
  </div>
</template>

<script setup>
import { useI18n } from 'vue-i18n'
import { setLocale } from '@/i18n'

const { locale, t } = useI18n()

const options = [
  { code: 'vi', flag: '🇻🇳' },
  { code: 'en', flag: '🇺🇸' },
]

function select(code) { setLocale(code) }
</script>

<style scoped lang="scss">
@use '../../assets/styles/variables' as *;

.lang-pill {
  display: inline-flex;
  align-items: center;
}

/* The outer track */
.pill-track {
  position: relative;
  display: flex;
  align-items: center;
  background: #F5F5F4;
  border: 1.5px solid #E7E5E4;
  border-radius: 30px;
  padding: 3px;
  transition: border-color 0.2s;
  user-select: none;

  &:hover { border-color: $primary-500; }
}

/* Animated white sliding thumb */
.pill-thumb {
  position: absolute;
  top: 3px;
  width: calc(50% - 3px);
  height: calc(100% - 6px);
  background: #fff;
  border-radius: 24px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.13);
  /* Bouncy spring transition */
  transition: left 0.28s cubic-bezier(0.34, 1.3, 0.64, 1);
  pointer-events: none;
  z-index: 0;
}

/* Each language button */
.pill-btn {
  position: relative;
  z-index: 1;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 24px;
  font-family: 'Mulish', sans-serif;
  font-size: 0.78rem;
  font-weight: 800;
  color: #A8A29E;
  letter-spacing: 0.4px;
  white-space: nowrap;
  transition: color 0.2s;

  &.active {
    color: #1C1917;
  }

  &:focus-visible {
    outline: 2px solid $primary-500;
    outline-offset: 2px;
    border-radius: 24px;
  }

  .pill-flag { font-size: 0.9rem; line-height: 1; }
  .pill-code { line-height: 1; }
}
</style>
