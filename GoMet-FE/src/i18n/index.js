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

export default i18n
