import { createI18n } from 'vue-i18n'
import vi from './locales/vi'
import en from './locales/en'

const savedLang = localStorage.getItem('gomet_lang') || 'vi'

export const i18n = createI18n({
  legacy: false,          // use Composition API mode
  locale: savedLang,
  fallbackLocale: 'vi',
  messages: { vi, en },
  globalInjection: true,  // inject $t, $locale globally
})

/** Switch language and persist choice */
export function setLocale(lang) {
  i18n.global.locale.value = lang
  localStorage.setItem('gomet_lang', lang)
  document.documentElement.setAttribute('lang', lang)
}

export function getDateLocale(lang = i18n.global.locale.value) {
  return lang === 'en' ? 'en-US' : 'vi-VN'
}

export function formatLocaleDate(date, options) {
  return new Date(date).toLocaleDateString(
    getDateLocale(),
    options || { day: '2-digit', month: '2-digit', year: 'numeric' },
  )
}

export function formatLocaleTime(date, options) {
  return new Date(date).toLocaleTimeString(
    getDateLocale(),
    options || { hour: '2-digit', minute: '2-digit' },
  )
}

export function formatLocaleDateTime(date, options) {
  return new Date(date).toLocaleString(
    getDateLocale(),
    options || { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' },
  )
}

export default i18n
