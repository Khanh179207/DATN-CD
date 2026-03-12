<template>
  <div class="gomet-profile-full" :style="profileThemeVars">
    
    <div class="bg-layer">
      <div class="grid-mesh"></div>
      <div class="noise-texture"></div>
    </div>

    <section class="profile-hero-banner" :class="premiumHeroClasses" :style="profileBannerStyle">
      <div class="profile-hero-overlay"></div>
      <div v-if="user.isPremium" class="profile-hero-motion profile-hero-motion-a"></div>
      <div v-if="user.isPremium" class="profile-hero-motion profile-hero-motion-b"></div>
      <div class="profile-hero-copy">
        <span class="hero-kicker">{{ user.isPremium ? 'GOMET PREMIUM PROFILE' : 'GOMET PROFILE' }}</span>
        <h2>{{ user.name || 'Chef' }}</h2>
        <p>{{ premiumHeadline }}</p>
      </div>
    </section>

    <div class="profile-layout-wide">
      
      <aside class="col-left">
        <div class="sticky-wrapper">
          <div class="id-card" :class="{ 'id-card-premium': user.isPremium }">
            <div class="avatar-box" :class="[{ 'avatar-premium': user.isPremium }, user.isPremium ? `avatar-frame-${premiumProfile.avatarFrame}` : '']">
              <img :src="user.avatar" class="avatar-img" alt="Chef">
              <div v-if="user.isPremium" class="premium-ring-anim" aria-hidden="true"></div>
              <div v-if="user.isPremium" class="premium-crown-badge" title="Premium Member">👑</div>
              <div v-else class="verify-badge">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3"><polyline points="20 6 9 17 4 12"></polyline></svg>
              </div>
            </div>
            
            <h1 class="user-name" :class="{ 'user-name-premium': user.isPremium }">{{ user.name }}</h1>
            <div v-if="user.isPremium" class="premium-title-badge">✦ GoMet Premium</div>
            <p class="user-handle">@{{ user.handle }}</p>
            
            <div class="bio-box">
              <p v-if="user.bio">{{ user.bio }}</p>
              <p v-else class="bio-placeholder">{{ isOwnProfile ? $t('profile.bio_empty_own') : $t('profile.bio_empty_other') }}</p>
            </div>

            <div v-if="user.createdAt" class="joined-info">
              <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
              {{ $t('profile.joined') }} {{ user.createdAt }}
            </div>

            <div class="action-stack">
              <button v-if="isOwnProfile" class="btn-primary" @click="openEditModal">{{ $t('profile.edit') }}</button>
              <button v-else class="btn-follow" :class="{ following: isFollowing }" @click="toggleFollow" :disabled="followLoading">
                <span v-if="followLoading">...</span>
                <span v-else-if="isFollowing">✓ {{ $t('common.following') }}</span>
                <span v-else>+ {{ $t('common.follow') }}</span>
              </button>
              <button class="btn-icon" type="button" @click="shareProfile" :title="isOwnProfile ? 'Sao chép link hồ sơ' : 'Chia sẻ hồ sơ'">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 12v8a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-8"/><polyline points="16 6 12 2 8 6"/><line x1="12" y1="2" x2="12" y2="15"/></svg>
              </button>
            </div>

            <div class="social-links" :class="{ 'social-links-premium': user.isPremium }">
              <template v-if="visibleSocialLinks.length">
                <a
                  v-for="link in visibleSocialLinks"
                  :key="link.key"
                  :href="link.url"
                  class="s-link s-link-icon"
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  <i :class="link.icon"></i>
                  <span>{{ link.label }}</span>
                </a>
              </template>
              <p v-else-if="user.isPremium && isOwnProfile" class="social-placeholder-premium">
                Thêm link Facebook, Instagram, Discord, TikTok hoặc website cá nhân của bạn.
              </p>
              <p v-else class="social-placeholder-premium social-placeholder-muted">
                Chưa có social links công khai.
              </p>
            </div>
          </div>
        </div>
      </aside>

      <main class="col-center">
        
        <div class="content-header">
          <h2 class="section-title">{{ $t('profile.masterpieces') }} <span class="count">{{ filteredPosts.length }}</span></h2>
          <div class="filter-tabs">
            <span 
              v-for="cat in postCategories" 
              :key="cat"
              class="tab" 
              :class="{ active: activeCategory === cat }"
              @click="activeCategory = cat"
            >{{ cat === 'All' ? $t('common.category_all') : cat }}</span>
          </div>
        </div>

        <div class="recipe-feed" :class="{ 'recipe-feed-premium': user.isPremium }">
          <RecipeCard 
            v-if="!postsLoading"
            v-for="post in filteredPosts" 
            :key="post.id" 
            :post="post" 
            class="feed-item"
          />
          <RecipeCard 
            v-if="postsLoading"
            v-for="n in 6" 
            :key="'sk-' + n" 
            :post="{}" 
            :loading="true"
            class="feed-item"
          />
        </div>

      </main>

      <aside class="col-right">
        <div class="sticky-wrapper">
          
          <div class="widget-box">
            <h3 class="w-title">{{ $t('profile.stats') }}</h3>
            <div class="stat-grid">
              <div class="stat-cell">
                <span class="val">{{ user.postsCount }}</span>
                <span class="lbl">{{ $t('profile.posts') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.followers }}</span>
                <span class="lbl">{{ $t('profile.followers') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.following }}</span>
                <span class="lbl">{{ $t('profile.following') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.totalLikes }}</span>
                <span class="lbl">{{ $t('profile.total_likes') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.totalViews }}</span>
                <span class="lbl">{{ $t('profile.total_views') }}</span>
              </div>
              <div class="stat-cell">
                <span class="val">{{ user.point }}</span>
                <span class="lbl">{{ $t('profile.points') }}</span>
              </div>
            </div>
          </div>

          <!-- Account Info widget -->
          <div class="widget-box">
            <h3 class="w-title">{{ $t('profile.account_info') }}</h3>
            <ul class="info-list">
              <li class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21a8 8 0 1 0-16 0"/><circle cx="12" cy="7" r="4"/></svg>
                <span>{{ $t('profile.display_name') }}</span>
                <strong>{{ user.name }}</strong>
              </li>
              <li v-if="isOwnProfile && user.email" class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M4 4h16v16H4z"/><path d="m22 6-10 7L2 6"/></svg>
                <span>{{ $t('profile.account_email') }}</span>
                <strong>{{ user.email }}</strong>
              </li>
              <li v-if="user.createdAt" class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M16 2v4M8 2v4M3 10h18"/></svg>
                <span>{{ $t('profile.joined') }}</span>
                <strong>{{ user.createdAt }}</strong>
              </li>
              <li class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v20"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7H14a3.5 3.5 0 0 1 0 7H6"/></svg>
                <span>{{ $t('profile.account_tier') }}</span>
                <strong>
                  <span class="status-badge" :class="accountTier.className">{{ accountTier.label }}</span>
                </strong>
              </li>
              <li class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M8 6h13"/><path d="M8 12h13"/><path d="M8 18h13"/><path d="M3 6h.01"/><path d="M3 12h.01"/><path d="M3 18h.01"/></svg>
                <span>{{ $t('profile.posts') }}</span>
                <strong>{{ user.postsCount }}</strong>
              </li>
              <li class="info-row">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="8.5" cy="7" r="4"/><path d="M20 8v6"/><path d="M23 11h-6"/></svg>
                <span>{{ $t('profile.followers') }}</span>
                <strong>{{ user.followers }}</strong>
              </li>
            </ul>
          </div>

          <div v-if="isOwnProfile" class="widget-box security-snapshot-card">
            <div class="widget-header-inline">
              <div>
                <h3 class="w-title w-title-no-border">{{ $t('profile.security_snapshot') }}</h3>
                <p class="snapshot-subtitle">{{ securityLevel.tip }}</p>
              </div>
              <button
                type="button"
                class="toggle-advanced-btn"
                @click="showAdvancedInfo = !showAdvancedInfo"
              >
                {{ showAdvancedInfo ? $t('profile.hide_advanced') : $t('profile.show_advanced') }}
              </button>
            </div>

            <transition name="advanced-panel">
              <div v-if="showAdvancedInfo" class="advanced-panel security-snapshot-body">
                <div class="snapshot-hero">
                  <div class="snapshot-score-ring" :style="securityRingStyle">
                    <div class="snapshot-score-core">
                      <span class="snapshot-score-number">{{ securityScore }}</span>
                      <span class="snapshot-score-label">{{ $t('profile.security_score') }}</span>
                    </div>
                  </div>

                  <div class="snapshot-hero-copy">
                    <div class="snapshot-badge-row">
                      <span class="status-badge" :class="riskState.className">{{ riskState.label }}</span>
                      <span class="status-badge" :class="securityLevel.className">{{ securityLevel.label }}</span>
                      <span class="status-badge" :class="mfaStatus.className">{{ mfaStatus.label }}</span>
                      <span class="snapshot-live-pill" :class="{ 'is-syncing': snapshotSyncing }">{{ realtimeStatusLabel }}</span>
                    </div>
                    <h4>{{ $t('profile.security_snapshot_title') }}</h4>
                    <p>{{ securityHeadline }}</p>
                  </div>
                </div>

                <div class="snapshot-grid">
                  <div class="snapshot-metric snapshot-metric-highlight">
                    <span class="snapshot-metric-label">{{ $t('profile.latest_device') }}</span>
                    <strong class="snapshot-metric-value">{{ latestAccessDevice?.name || $t('profile.unknown_device') }}</strong>
                    <p class="snapshot-metric-note">
                      <span v-if="latestAccessDevice?.lastSeenAt">{{ formatDateTime(latestAccessDevice.lastSeenAt) }}</span>
                      <span v-else>{{ $t('profile.login_unknown') }}</span>
                      <span v-if="latestAccessDevice?.trusted"> · {{ $t('profile.device_trusted') }}</span>
                      <span v-else-if="latestAccessDevice"> · {{ $t('profile.device_review') }}</span>
                    </p>
                  </div>

                  <div class="snapshot-metric">
                    <span class="snapshot-metric-label">{{ $t('profile.trusted_devices') }}</span>
                    <strong class="snapshot-metric-value">{{ trustedDevices.length }}</strong>
                    <p class="snapshot-metric-note">
                      {{ latestTrustedDevice?.deviceName || $t('profile.trusted_device_none') }}
                    </p>
                  </div>

                  <div class="snapshot-metric">
                    <span class="snapshot-metric-label">{{ $t('profile.active_sessions') }}</span>
                    <strong class="snapshot-metric-value">{{ activeSessions.length }}</strong>
                    <p class="snapshot-metric-note">{{ activeSessionsHint }}</p>
                  </div>

                  <div class="snapshot-metric">
                    <span class="snapshot-metric-label">{{ $t('profile.risk_engine') }}</span>
                    <strong class="snapshot-metric-value">
                      <span class="status-badge" :class="riskState.className">{{ riskState.label }}</span>
                    </strong>
                    <p class="snapshot-metric-note">{{ riskState.tip }}</p>
                  </div>

                  <div class="snapshot-metric">
                    <span class="snapshot-metric-label">{{ $t('profile.security_level') }}</span>
                    <strong class="snapshot-metric-value">
                      <span class="status-badge" :class="securityLevel.className">{{ securityLevel.label }}</span>
                    </strong>
                    <p class="snapshot-metric-note">{{ securityLevel.tip }}</p>
                  </div>
                </div>

                <div class="snapshot-details-grid">
                  <div class="advanced-card advanced-card-highlight">
                    <span class="advanced-label">{{ $t('profile.last_login') }}</span>
                    <strong class="advanced-value">{{ user.lastLoginAt || '-' }}</strong>
                    <p class="advanced-note">{{ loginFreshness }}</p>
                  </div>

                  <div class="advanced-card">
                    <span class="advanced-label">{{ $t('profile.last_ip') }}</span>
                    <strong class="advanced-value mono-text">{{ latestKnownIp || '-' }}</strong>
                    <p class="advanced-note">{{ latestKnownIp ? $t('profile.ip_tip_known') : $t('profile.ip_tip_unknown') }}</p>
                  </div>

                  <div class="advanced-card">
                    <span class="advanced-label">{{ $t('profile.mfa_status') }}</span>
                    <strong class="advanced-value">
                      <span class="status-badge" :class="mfaStatus.className">{{ mfaStatus.label }}</span>
                    </strong>
                    <p class="advanced-note">{{ mfaStatus.tip }}</p>
                  </div>

                  <div class="advanced-card">
                    <span class="advanced-label">{{ $t('profile.backup_codes') }}</span>
                    <strong class="advanced-value">{{ backupCodesLabel }}</strong>
                    <p class="advanced-note">{{ backupCodesHint }}</p>
                  </div>
                </div>

                <div class="snapshot-watchlist">
                  <div class="snapshot-watchlist-head">
                    <h4>{{ $t('profile.security_watchlist') }}</h4>
                    <span class="snapshot-watchlist-count">{{ securityWarnings.length }}</span>
                  </div>

                  <div v-if="securityWarnings.length" class="snapshot-alert-list">
                    <div
                      v-for="warning in securityWarnings"
                      :key="warning.key"
                      class="snapshot-alert"
                      :class="`snapshot-alert-${warning.severity}`"
                    >
                      <div class="snapshot-alert-icon">{{ warning.icon }}</div>
                      <div>
                        <strong>{{ warning.title }}</strong>
                        <p>{{ warning.description }}</p>
                      </div>
                    </div>
                  </div>

                  <div v-else class="snapshot-alert snapshot-alert-ok">
                    <div class="snapshot-alert-icon">✓</div>
                    <div>
                      <strong>{{ $t('profile.security_all_clear_title') }}</strong>
                      <p>{{ $t('profile.security_all_clear_desc') }}</p>
                    </div>
                  </div>
                </div>

                <div v-if="riskReasons.length" class="snapshot-insights">
                  <div class="snapshot-watchlist-head">
                    <h4>{{ $t('profile.risk_signals') }}</h4>
                    <span class="snapshot-watchlist-count">{{ riskReasons.length }}</span>
                  </div>

                  <div class="snapshot-insight-list">
                    <div
                      v-for="reason in riskReasons"
                      :key="reason.key"
                      class="snapshot-insight"
                      :class="`snapshot-insight-${reason.tone}`"
                    >
                      <div class="snapshot-insight-icon">{{ reason.icon }}</div>
                      <div>
                        <strong>{{ reason.title }}</strong>
                        <p>{{ reason.description }}</p>
                      </div>
                    </div>
                  </div>
                </div>

                <div v-if="priorityActions.length" class="snapshot-priority-actions">
                  <div class="snapshot-watchlist-head">
                    <h4>{{ $t('profile.priority_actions') }}</h4>
                    <span class="snapshot-watchlist-count">{{ priorityActions.length }}</span>
                  </div>

                  <div class="snapshot-priority-list">
                    <component
                      :is="action.kind === 'route' ? 'router-link' : 'button'"
                      v-for="action in priorityActions"
                      :key="action.key"
                      class="snapshot-priority-card"
                      :class="`snapshot-priority-${action.tone}`"
                      :to="action.kind === 'route' ? action.to : undefined"
                      :type="action.kind === 'button' ? 'button' : undefined"
                      @click="action.kind === 'button' ? runPriorityAction(action.key) : undefined"
                    >
                      <div>
                        <strong>{{ action.label }}</strong>
                        <p>{{ action.note }}</p>
                      </div>
                      <span class="snapshot-priority-arrow">{{ action.kind === 'route' ? '→' : '•' }}</span>
                    </component>
                  </div>
                </div>

                <div class="snapshot-actions">
                  <button class="snapshot-action-btn" :disabled="snapshotSyncing" @click="refreshSecurityRealtime">
                    {{ $t('profile.refresh_snapshot') }}
                  </button>
                  <button class="snapshot-action-btn warning" :disabled="!latestTrustedDevice?.id || snapshotSyncing" @click="openDangerConfirm('review_latest_device')">
                    {{ $t('profile.revoke_latest_device') }}
                  </button>
                  <button class="snapshot-action-btn danger" :disabled="!suspiciousSessions.length || snapshotSyncing" @click="openDangerConfirm('review_sessions')">
                    {{ $t('profile.revoke_suspicious_sessions') }}
                  </button>
                </div>

                <div class="snapshot-sync-meta">
                  <span>{{ lastRealtimeSyncLabel }}</span>
                  <span>{{ riskState.tip }}</span>
                </div>

                <div v-if="hasLiveEventPreview" class="snapshot-live-event">
                  <div class="snapshot-live-event-dot"></div>
                  <div>
                    <strong>{{ $t('profile.live_event_arrived') }}</strong>
                    <p>{{ liveEventPreview.title }} · {{ liveEventPreview.time }}</p>
                  </div>
                </div>

                <div class="snapshot-events">
                  <div class="snapshot-events-head">
                    <h4>{{ $t('profile.security_events') }}</h4>
                    <span class="snapshot-events-count">{{ timelineEvents.length }}</span>
                  </div>

                  <div v-if="securityFeedLoading && !timelineEvents.length" class="snapshot-events-empty">
                    {{ $t('common.loading') }}
                  </div>

                  <div v-else-if="timelineEvents.length" class="snapshot-event-list">
                    <div
                      v-for="event in timelineEvents"
                      :key="event.id"
                      class="snapshot-event-item"
                      :class="[`snapshot-event-${event.severity}`, { 'is-live': newestLiveEventId === event.id }]"
                    >
                      <div class="snapshot-event-marker">{{ event.icon }}</div>
                      <div class="snapshot-event-copy">
                        <div class="snapshot-event-headline">
                          <strong>{{ event.title }}</strong>
                          <span>{{ event.time }}</span>
                        </div>
                        <p>{{ event.description }}</p>
                        <small>{{ $t('profile.last_ip') }}: {{ event.ip }}</small>
                      </div>
                    </div>
                  </div>

                  <div v-else class="snapshot-events-empty">
                    {{ $t('profile.security_events_empty') }}
                  </div>
                </div>

                <div class="snapshot-timeline">
                  <div class="snapshot-line">
                    <span>{{ $t('profile.latest_trusted_device') }}</span>
                    <strong>{{ latestTrustedDevice?.deviceName || $t('profile.trusted_device_none') }}</strong>
                  </div>
                  <div class="snapshot-line">
                    <span>{{ $t('profile.last_seen') }}</span>
                    <strong>{{ latestTrustedDevice?.lastSeenAt ? formatDateTime(latestTrustedDevice.lastSeenAt) : '-' }}</strong>
                  </div>
                  <div class="snapshot-line">
                    <span>{{ $t('profile.latest_device_ip') }}</span>
                    <strong class="mono-text">{{ latestAccessDevice?.ip || latestKnownIp || '-' }}</strong>
                  </div>
                </div>

                <router-link to="/settings/security" class="advanced-link">
                  {{ $t('profile.open_security_settings') }}
                </router-link>
              </div>
            </transition>
          </div>

          <div class="widget-box">
            <h3 class="w-title">{{ $t('profile.achievements') }}</h3>
            <div v-if="achievements.length === 0" class="award-empty">
              <span class="icon">
                <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
              </span>
              <span>{{ $t('profile.no_achievements') }}</span>
            </div>
            <div v-else class="achievement-grid">
              <div v-for="ach in achievements" :key="ach.uaid" class="ach-card" :title="ach.description">
                <div class="ach-icon-wrap">
                  <span class="ach-icon">{{ ach.icon || '🏆' }}</span>
                </div>
                <div class="ach-info">
                  <strong>{{ ach.achievementName }}</strong>
                  <span>{{ ach.description }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="widget-box" v-if="isOwnProfile">
            <h3 class="w-title">Bằng/Chứng nhận</h3>
            <div v-if="certificatesLoading" class="award-empty">
              <span class="icon">⏳</span>
              <span>Đang tải chứng nhận...</span>
            </div>
            <div v-else-if="certificates.length === 0" class="award-empty">
              <span class="icon">📄</span>
              <span>Chưa có chứng nhận tuần.</span>
            </div>
            <div v-else class="achievement-grid">
              <div v-for="cert in certificates" :key="cert.id" class="ach-card">
                <div class="ach-icon-wrap"><span class="ach-icon">🏅</span></div>
                <div class="ach-info">
                  <strong>#{{ cert.rank }} - {{ cert.title }}</strong>
                  <span>{{ cert.weekStart }} → {{ cert.weekEnd }} • Score: {{ cert.score }}</span>
                  <span>{{ cert.certificateCode }}</span>
                </div>
              </div>
            </div>
          </div>

        </div>
      </aside>

    </div>
  </div>

  <!-- Edit Profile Modal -->
  <teleport to="body">
    <transition name="modal-fade">
      <div v-if="showEditModal" class="edit-modal-overlay" @click.self="showEditModal = false">
        <div class="edit-modal-card">
          <div class="edit-modal-header">
            <h2>{{ $t('profile.edit') }}</h2>
            <button class="btn-close" @click="showEditModal = false">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
            </button>
          </div>

          <div class="edit-modal-body">
            <!-- Avatar preview -->
            <div class="edit-avatar-section">
              <div class="edit-avatar-wrap">
                <img :src="editForm.avatarPreview || user.avatar" class="edit-avatar-img" alt="Avatar">
                <label class="edit-avatar-overlay" title="Change photo">
                  <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"></path><circle cx="12" cy="13" r="4"></circle></svg>
                  <input type="file" accept="image/*" class="file-hidden" @change="onAvatarChange">
                </label>
              </div>
              <p class="avatar-hint">{{ $t('profile.hint_avatar') }}</p>
            </div>

            <div class="edit-field">
              <label>{{ $t('profile.label_name') }}</label>
              <input v-model="editForm.username" type="text" :placeholder="$t('profile.label_name')" maxlength="50">
            </div>

            <div class="edit-field">
              <label>{{ $t('profile.label_bio') }}</label>
              <textarea v-model="editForm.bio" :placeholder="$t('profile.bio_placeholder')" rows="4" maxlength="300"></textarea>
              <span class="char-count">{{ editForm.bio.length }} / 300</span>
            </div>

            <div v-if="user.isPremium" class="premium-edit-panel">
              <div class="premium-edit-head">
                <div>
                  <h3>Premium Profile Studio</h3>
                  <p>Tùy biến banner, frame, gradient tên, màu nền và social links có icon. Avatar GIF và banner GIF đều hiển thị động.</p>
                </div>
                <span class="premium-pill-inline">VIP PRO</span>
              </div>

              <div class="premium-banner-editor">
                <div class="premium-banner-preview" :style="editBannerPreviewStyle">
                  <div class="premium-banner-preview-copy">
                    <span>Banner preview</span>
                    <strong>{{ editForm.username || user.name || 'GoMet Premium' }}</strong>
                  </div>
                </div>

                <div class="premium-banner-actions">
                  <label class="premium-upload-btn">
                    <i class="fa-solid fa-panorama"></i>
                    <span>Tải banner / GIF</span>
                    <input type="file" accept="image/*" class="file-hidden" @change="onBannerChange">
                  </label>
                  <button type="button" class="premium-reset-btn" @click="clearBanner">
                    Xóa banner
                  </button>
                </div>
              </div>

              <div class="premium-theme-grid">
                <div class="edit-field premium-field-compact">
                  <label>Avatar frame</label>
                  <select v-model="editForm.avatarFrame" class="premium-select">
                    <option v-for="frame in premiumFrameOptions" :key="frame.value" :value="frame.value">{{ frame.label }}</option>
                  </select>
                </div>

                <div class="edit-field premium-field-compact">
                  <label>Màu chính</label>
                  <div class="premium-color-input">
                    <input v-model="editForm.themePrimary" type="color">
                    <span>{{ editForm.themePrimary }}</span>
                  </div>
                </div>

                <div class="edit-field premium-field-compact">
                  <label>Màu phụ</label>
                  <div class="premium-color-input">
                    <input v-model="editForm.themeSecondary" type="color">
                    <span>{{ editForm.themeSecondary }}</span>
                  </div>
                </div>

                <div class="edit-field premium-field-compact">
                  <label>Màu accent</label>
                  <div class="premium-color-input">
                    <input v-model="editForm.accentColor" type="color">
                    <span>{{ editForm.accentColor }}</span>
                  </div>
                </div>

                <div class="edit-field premium-field-compact premium-field-wide">
                  <label>Màu card bài viết</label>
                  <div class="premium-color-input">
                    <input v-model="editForm.cardBackground" type="color">
                    <span>{{ editForm.cardBackground }}</span>
                  </div>
                </div>
              </div>

              <div class="premium-preset-strip">
                <button
                  v-for="preset in premiumThemePresets"
                  :key="preset.key"
                  type="button"
                  class="premium-preset-card"
                  :style="{ '--preset-a': preset.themePrimary, '--preset-b': preset.themeSecondary, '--preset-c': preset.cardBackground }"
                  @click="applyThemePreset(preset)"
                >
                  <span class="premium-preset-name">{{ preset.label }}</span>
                  <span class="premium-preset-meta">{{ preset.avatarFrameLabel }}</span>
                </button>
              </div>

              <div class="premium-live-preview" :style="editBannerPreviewStyle">
                <div class="premium-live-preview-overlay"></div>
                <div class="premium-live-preview-content">
                  <div class="premium-live-avatar" :class="[`avatar-frame-${editForm.avatarFrame}`, `share-style-${editForm.shareCardStyle}`]">
                    <img :src="editForm.avatarPreview || user.avatar" alt="Preview avatar">
                    <div class="premium-live-ring"></div>
                  </div>

                  <div class="premium-live-copy">
                    <span class="premium-live-kicker">Live premium preview</span>
                    <strong :style="previewUsernameStyle">{{ editForm.username || user.name || 'GoMet Premium' }}</strong>
                    <p>{{ editForm.bio || 'Banner động, gradient username, card màu riêng và social icon sẽ hiển thị như phần preview này.' }}</p>

                    <div class="premium-live-links">
                      <span
                        v-for="link in editVisibleSocialLinks"
                        :key="link.key"
                        class="premium-live-link"
                      >
                        <i :class="link.icon"></i>
                        {{ link.label }}
                      </span>
                    </div>

                    <div class="premium-live-card" :style="previewCardStyle">
                      <span>Recipe card preview</span>
                      <strong>Chef Signature Collection</strong>
                    </div>
                  </div>
                </div>
              </div>

              <div class="premium-share-studio">
                <div class="premium-share-headline">
                  <div>
                    <h4>Social Share Card</h4>
                    <p>Preset public mini card để chụp màn hình hoặc share link lên Discord, Facebook, Messenger.</p>
                  </div>
                  <div class="premium-share-actions">
                    <button type="button" class="premium-copy-btn" @click="copyShareCaption">Copy caption</button>
                    <button type="button" class="premium-copy-btn" @click="exportShareCardImage">Export image</button>
                  </div>
                </div>

                <div class="premium-share-presets">
                  <button
                    v-for="preset in shareCardPresets"
                    :key="preset.value"
                    type="button"
                    class="premium-share-preset"
                    :class="{ active: editForm.shareCardStyle === preset.value }"
                    @click="editForm.shareCardStyle = preset.value"
                  >
                    <strong>{{ preset.label }}</strong>
                    <span>{{ preset.sub }}</span>
                  </button>
                </div>

                <div ref="shareCardExportRef" class="premium-social-card-preview" :class="[`share-style-${editForm.shareCardStyle}`, `avatar-frame-${editForm.avatarFrame}`]">
                  <div class="social-card-glow"></div>
                  <div class="social-card-avatar-wrap">
                    <img :src="editForm.avatarPreview || user.avatar" alt="Social card avatar">
                    <div class="social-card-ring"></div>
                  </div>

                  <div class="social-card-copy">
                    <span class="social-card-badge">GoMet Premium</span>
                    <strong :style="previewUsernameStyle">{{ editForm.username || user.name || 'GoMet Premium' }}</strong>
                    <p>{{ socialCardDescription }}</p>

                    <div class="social-card-links">
                      <span v-for="link in editVisibleSocialLinks.slice(0, 4)" :key="link.key" class="social-card-link-pill">
                        <i :class="link.icon"></i>
                        {{ link.label }}
                      </span>
                    </div>
                  </div>
                </div>

                <div class="premium-share-caption-box">
                  <span class="caption-label">Caption preview</span>
                  <p>{{ shareCaption }}</p>
                </div>
              </div>

              <div class="premium-social-grid">
                <div v-for="platform in socialPlatformOptions" :key="platform.key" class="edit-field premium-field-compact">
                  <label>
                    <i :class="platform.icon"></i>
                    {{ platform.label }}
                  </label>
                  <input v-model="editForm.socialLinks[platform.key]" type="url" :placeholder="platform.placeholder" maxlength="1000">
                </div>
              </div>
            </div>

            <div v-else class="premium-upsell-note">
              Premium mở khóa khung avatar động, banner ảnh/GIF, gradient username, đổi màu nền profile, màu card bài viết và link social có icon.
            </div>
          </div>

          <div class="edit-modal-footer">
            <button class="btn-cancel" @click="showEditModal = false">{{ $t('common.cancel') }}</button>
            <button class="btn-save" :disabled="editSaving" @click="saveProfile">
              <span v-if="editSaving" class="spinner-sm"></span>
              <span>{{ editSaving ? $t('profile.saving') : $t('profile.save_changes') }}</span>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </teleport>

  <ConfirmDangerModal
    v-model="dangerConfirm.open"
    :title="dangerConfirm.title"
    :description="dangerConfirm.description"
    :highlights="dangerConfirm.highlights"
    :confirm-label="dangerConfirm.confirmLabel"
    :loading="dangerConfirm.loading"
    @cancel="closeDangerConfirm"
    @confirm="confirmDangerAction"
  />
</template>

<script setup>
import { toPng } from 'html-to-image'
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import RecipeCard from '@/components/common/RecipeCard.vue'
import ConfirmDangerModal from '@/components/common/ConfirmDangerModal.vue'
import { useSecurityMonitor } from '@/composables/useSecurityMonitor'
import { getUserProfile, updateUserProfile } from '@/services/userService'
import { getPostsByUser, normalizePost } from '@/services/postService'
import { useAuthStore } from '@/stores/auth'
import { getUserAchievements } from '@/services/achievementService'
import { checkFollow, follow, unfollow } from '@/services/socialService'
import { uploadMedia } from '@/services/uploadService'
import { toast } from '@/composables/useToast'
import { getMyCertificates } from '@/services/certificateService'

const route = useRoute()
const authStore = useAuthStore()
const { t, locale } = useI18n()
const apiBaseUrl = (import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080').replace(/\/$/, '')

const socialPlatformOptions = [
  { key: 'facebook', label: 'Facebook', icon: 'fa-brands fa-facebook-f', placeholder: 'https://facebook.com/your-profile' },
  { key: 'instagram', label: 'Instagram', icon: 'fa-brands fa-instagram', placeholder: 'https://instagram.com/your-profile' },
  { key: 'discord', label: 'Discord', icon: 'fa-brands fa-discord', placeholder: 'https://discord.gg/your-server' },
  { key: 'tiktok', label: 'TikTok', icon: 'fa-brands fa-tiktok', placeholder: 'https://www.tiktok.com/@your-profile' },
  { key: 'youtube', label: 'YouTube', icon: 'fa-brands fa-youtube', placeholder: 'https://youtube.com/@your-channel' },
  { key: 'website', label: 'Website', icon: 'fa-solid fa-globe', placeholder: 'https://your-site.com' }
]

const premiumFrameOptions = [
  { value: 'gold', label: 'Gold Orbit' },
  { value: 'aurora', label: 'Aurora Pulse' },
  { value: 'neon', label: 'Neon Flux' },
  { value: 'cosmic', label: 'Cosmic Wave' }
]

const premiumThemePresets = [
  { key: 'ember-luxe', label: 'Ember Luxe', avatarFrame: 'gold', avatarFrameLabel: 'Gold Orbit', themePrimary: '#F59E0B', themeSecondary: '#F97316', accentColor: '#111827', cardBackground: '#FFF7ED' },
  { key: 'rose-royal', label: 'Rose Royal', avatarFrame: 'neon', avatarFrameLabel: 'Neon Flux', themePrimary: '#EC4899', themeSecondary: '#8B5CF6', accentColor: '#1F1147', cardBackground: '#FDF2F8' },
  { key: 'aqua-aurora', label: 'Aqua Aurora', avatarFrame: 'aurora', avatarFrameLabel: 'Aurora Pulse', themePrimary: '#06B6D4', themeSecondary: '#3B82F6', accentColor: '#082F49', cardBackground: '#ECFEFF' },
  { key: 'cosmic-night', label: 'Cosmic Night', avatarFrame: 'cosmic', avatarFrameLabel: 'Cosmic Wave', themePrimary: '#6366F1', themeSecondary: '#A855F7', accentColor: '#0F172A', cardBackground: '#EEF2FF' }
]

const shareCardPresets = [
  { value: 'spotlight', label: 'Spotlight', sub: 'Hero card nổi bật, hợp story và cover' },
  { value: 'stacked', label: 'Stacked', sub: 'Card dọc gọn gàng, hợp bài đăng feed' },
  { value: 'minimal', label: 'Minimal', sub: 'Clean card, nhấn username và link' },
  { value: 'creator', label: 'Creator', sub: 'Đậm chất creator profile, hợp Discord' }
]

const user = ref({
  name: '', handle: '', avatar: '', bio: '',
  email: '',
  isPremium: false,
  postsCount: 0, followers: '0', following: 0, point: 0, totalLikes: 0,
  totalViews: 0, createdAt: '', lastLoginAtRaw: '', lastLoginAt: '', lastLoginIp: '', mfaEnabled: 0,
  premiumProfile: createEmptyPremiumProfile()
})
const allPosts = ref([])
const postsLoading = ref(true)
const activeCategory = ref('All')
const achievements = ref([])
const certificates = ref([])
const certificatesLoading = ref(false)
const isFollowing = ref(false)
const followLoading = ref(false)
const showAdvancedInfo = ref(false)
const liveEventPreview = ref(null)
let liveEventPreviewTimer = null
const dangerConfirm = ref({ open: false, action: '', title: '', description: '', highlights: [], confirmLabel: '', loading: false })

const {
  sessions: activeSessions,
  devices: trustedDevices,
  securityEvents,
  securityRisk,
  securityFeedLoading,
  snapshotSyncing,
  snapshotUpdatedAt,
  streamStatus,
  mfaStatus: securityMfaStatus,
  suspiciousSessions,
  refreshSecurityState,
  startLiveStream,
  stopLiveStream,
  clearState: clearSecurityState,
  revokeSession: revokeSecuritySession,
  revokeDevice: revokeSecurityDevice
} = useSecurityMonitor({
  eventPageSize: 12,
  getCurrentRefreshToken: () => localStorage.getItem('refreshToken') || authStore.user?.refreshToken || null,
  onLiveEvent: event => showLiveEventPreview(event)
})

const localeTag = computed(() => locale.value === 'en' ? 'en-US' : 'vi-VN')

// Derived: unique categories from loaded posts
const postCategories = computed(() => {
  const cats = new Set(allPosts.value.map(p => p.category).filter(Boolean))
  return ['All', ...Array.from(cats)]
})

// Filtered posts based on active category tab
const filteredPosts = computed(() => {
  if (activeCategory.value === 'All') return allPosts.value
  return allPosts.value.filter(p => p.category === activeCategory.value)
})

// Edit modal state
const showEditModal = ref(false)
const editSaving = ref(false)
const editForm = ref(createEditForm())
const shareCardExportRef = ref(null)

const isOwnProfile = computed(() => {
  const myId = authStore.user?.accountID || authStore.user?.id
  const paramId = route.params.id
  return !paramId || String(paramId) === String(myId)
})

const accountTier = computed(() => {
  if (user.value.isAdmin) {
    return { label: 'Admin', className: 'tier-admin' }
  }
  if (user.value.isPremium) {
    return { label: 'Premium', className: 'tier-premium' }
  }
  return { label: 'Member', className: 'tier-member' }
})

const premiumProfile = computed(() => user.value.premiumProfile || createEmptyPremiumProfile())

const premiumHeroClasses = computed(() => ({
  'is-premium': user.value.isPremium,
  [`hero-motion-${premiumProfile.value.avatarFrame}`]: user.value.isPremium
}))

const visibleSocialLinks = computed(() => {
  return socialPlatformOptions
    .filter(platform => premiumProfile.value.socialLinks[platform.key])
    .map(platform => ({
      ...platform,
      url: premiumProfile.value.socialLinks[platform.key]
    }))
})

const premiumHeadline = computed(() => {
  if (user.value.isPremium) {
    return 'Avatar GIF, banner động, gradient username, profile theme custom và social links icon đều đang bật.'
  }
  return 'Không gian hồ sơ để showcase công thức, thành tích và dấu ấn cá nhân của bạn.'
})

const profileThemeVars = computed(() => ({
  '--premium-theme-primary': premiumProfile.value.themePrimary,
  '--premium-theme-secondary': premiumProfile.value.themeSecondary,
  '--premium-theme-accent': premiumProfile.value.accentColor,
  '--premium-card-background': premiumProfile.value.cardBackground
}))

const profileBannerStyle = computed(() => {
  const bannerUrl = premiumProfile.value.bannerUrl
  if (bannerUrl) {
    return {
      backgroundImage: `linear-gradient(135deg, rgba(15, 23, 42, 0.28), rgba(15, 23, 42, 0.58)), url(${bannerUrl})`
    }
  }

  return {
    backgroundImage: `linear-gradient(135deg, ${premiumProfile.value.themePrimary}, ${premiumProfile.value.themeSecondary})`
  }
})

const editBannerPreviewStyle = computed(() => {
  const bannerUrl = editForm.value.bannerPreview || (!editForm.value.removeBanner ? premiumProfile.value.bannerUrl : '')
  if (bannerUrl) {
    return {
      backgroundImage: `linear-gradient(135deg, rgba(15, 23, 42, 0.2), rgba(15, 23, 42, 0.52)), url(${bannerUrl})`
    }
  }

  return {
    backgroundImage: `linear-gradient(135deg, ${editForm.value.themePrimary}, ${editForm.value.themeSecondary})`
  }
})

const editVisibleSocialLinks = computed(() => {
  return socialPlatformOptions
    .filter(platform => String(editForm.value.socialLinks?.[platform.key] || '').trim())
    .map(platform => ({
      ...platform,
      url: String(editForm.value.socialLinks?.[platform.key] || '').trim()
    }))
})

const previewUsernameStyle = computed(() => ({
  backgroundImage: `linear-gradient(135deg, ${editForm.value.themePrimary}, ${editForm.value.themeSecondary})`
}))

const previewCardStyle = computed(() => ({
  background: `linear-gradient(180deg, rgba(255,255,255,0.95), ${editForm.value.cardBackground})`,
  color: editForm.value.accentColor,
  borderColor: editForm.value.themePrimary
}))

const socialCardDescription = computed(() => {
  const base = editForm.value.bio || user.value.bio || 'Premium creator trên GoMet, nơi chia sẻ công thức và dấu ấn cá nhân.'
  return base.length > 120 ? `${base.slice(0, 117)}...` : base
})

const shareProfileUrl = computed(() => {
  const profileId = route.params.id || authStore.user?.accountID || authStore.user?.id
  return `${apiBaseUrl}/share/profile/${profileId}`
})

const shareCaption = computed(() => {
  return `${editForm.value.username || user.value.name} trên GoMet\n${socialCardDescription.value}\n${shareProfileUrl.value}`
})

const isMfaEnabled = computed(() => Boolean(securityMfaStatus.mfaEnabled))

const latestKnownIp = computed(() => securityRisk.value?.latestIp || user.value.lastLoginIp || '')

const mfaSummary = computed(() => ({ remainingBackupCodes: securityMfaStatus.remainingBackupCodes }))

const lastLoginAgeDays = computed(() => {
  if (!user.value.lastLoginAtRaw) {
    return null
  }

  const lastLogin = new Date(user.value.lastLoginAtRaw)
  if (Number.isNaN(lastLogin.getTime())) {
    return null
  }

  return Math.floor((Date.now() - lastLogin.getTime()) / (1000 * 60 * 60 * 24))
})

const latestTrustedDevice = computed(() => {
  return [...trustedDevices.value].sort((left, right) => {
    const rightTime = new Date(right.lastSeenAt || right.firstSeenAt || 0).getTime()
    const leftTime = new Date(left.lastSeenAt || left.firstSeenAt || 0).getTime()
    return rightTime - leftTime
  })[0] || null
})

const trustedDeviceIds = computed(() => new Set(trustedDevices.value.map(device => device.deviceId).filter(Boolean)))

const latestSession = computed(() => {
  return [...activeSessions.value].sort((left, right) => {
    const rightTime = new Date(right.lastUsedAt || right.createdAt || 0).getTime()
    const leftTime = new Date(left.lastUsedAt || left.createdAt || 0).getTime()
    return rightTime - leftTime
  })[0] || null
})

const latestAccessDevice = computed(() => {
  if (latestSession.value) {
    const trustedMatch = trustedDevices.value.find(device => device.deviceId && device.deviceId === latestSession.value.deviceId)
    return {
      name: latestSession.value.deviceName || latestSession.value.userAgent || t('profile.unknown_device'),
      ip: latestSession.value.ip || trustedMatch?.lastIp || latestKnownIp.value || '',
      lastSeenAt: latestSession.value.lastUsedAt || latestSession.value.createdAt || '',
      trusted: securityRisk.value?.deviceTrusted ?? Boolean(trustedMatch),
      current: Boolean(latestSession.value.current)
    }
  }

  if (latestTrustedDevice.value) {
    return {
      name: latestTrustedDevice.value.deviceName || t('profile.unknown_device'),
      ip: latestTrustedDevice.value.lastIp || latestKnownIp.value || '',
      lastSeenAt: latestTrustedDevice.value.lastSeenAt || latestTrustedDevice.value.firstSeenAt || '',
      trusted: true,
      current: false
    }
  }

  return null
})

const hasUnfamiliarIp = computed(() => {
  if (typeof securityRisk.value?.ipChanged === 'boolean') {
    return securityRisk.value.ipChanged
  }

  if (!latestKnownIp.value || trustedDevices.value.length === 0) {
    return false
  }

  return !trustedDevices.value.some(device => device.lastIp && device.lastIp === latestKnownIp.value)
})

const activeSessionCount = computed(() => securityRisk.value?.activeSessionCount ?? activeSessions.value.length)

const trustedDeviceCount = computed(() => securityRisk.value?.trustedDeviceCount ?? trustedDevices.value.length)

const hasTooManySessions = computed(() => activeSessionCount.value >= 4)

const backupCodesLabel = computed(() => {
  if (mfaSummary.value.remainingBackupCodes == null) {
    return '-'
  }
  return String(mfaSummary.value.remainingBackupCodes)
})

const backupCodesHint = computed(() => {
  if (!isMfaEnabled.value) {
    return t('profile.backup_codes_disabled_hint')
  }
  if (mfaSummary.value.remainingBackupCodes == null) {
    return t('profile.backup_codes_unknown_hint')
  }
  return t('profile.backup_codes_hint', { count: mfaSummary.value.remainingBackupCodes })
})

const mfaStatus = computed(() => {
  if (isMfaEnabled.value) {
    return {
      label: t('profile.mfa_on_friendly'),
      className: 'security-strong',
      tip: t('profile.mfa_tip_on')
    }
  }
  return {
    label: t('profile.mfa_off_friendly'),
    className: 'security-review',
    tip: t('profile.mfa_tip_off')
  }
})

const riskState = computed(() => {
  const level = securityRisk.value?.riskLevel || 'MEDIUM'

  if (level === 'LOW') {
    return {
      level: 'LOW',
      label: t('profile.risk_low'),
      className: 'risk-low',
      tip: t('profile.risk_low_tip')
    }
  }

  if (level === 'HIGH') {
    return {
      level: 'HIGH',
      label: t('profile.risk_high'),
      className: 'risk-high',
      tip: t('profile.risk_high_tip')
    }
  }

  return {
    level: 'MEDIUM',
    label: t('profile.risk_medium'),
    className: 'risk-medium',
    tip: t('profile.risk_medium_tip')
  }
})

const securityLevel = computed(() => {
  if (securityScore.value >= 85) {
    return {
      label: t('profile.security_strong'),
      className: 'security-strong',
      tip: t('profile.security_snapshot_subtitle_strong')
    }
  }
  if (securityScore.value >= 68) {
    return {
      label: t('profile.security_good'),
      className: 'security-good',
      tip: t('profile.security_snapshot_subtitle_good')
    }
  }
  if (securityScore.value >= 45) {
    return {
      label: t('profile.security_review'),
      className: 'security-review',
      tip: t('profile.security_snapshot_subtitle_review')
    }
  }
  return {
    label: t('profile.security_unknown'),
    className: 'security-muted',
    tip: t('profile.security_snapshot_subtitle_unknown')
  }
})

const securityScore = computed(() => {
  let score = 42

  if (isMfaEnabled.value) score += 22
  if (trustedDeviceCount.value > 0) score += Math.min(18, 10 + trustedDeviceCount.value * 2)
  if (activeSessionCount.value > 0 && activeSessionCount.value <= 2) score += 10
  if (latestAccessDevice.value?.trusted) score += 8
  if (!hasUnfamiliarIp.value && latestKnownIp.value) score += 8
  if (lastLoginAgeDays.value != null && lastLoginAgeDays.value <= 14) score += 10
  if (lastLoginAgeDays.value != null && lastLoginAgeDays.value > 30) score -= 12
  if (hasTooManySessions.value) score -= 10
  if (!isMfaEnabled.value) score -= 10
  if (hasUnfamiliarIp.value) score -= 14
  if (riskState.value.level === 'HIGH') score -= 12
  if (riskState.value.level === 'MEDIUM') score -= 4

  return Math.max(0, Math.min(100, score))
})

const securityRingStyle = computed(() => {
  const accent = securityScore.value >= 85 ? '#16A34A' : securityScore.value >= 68 ? '#2563EB' : securityScore.value >= 45 ? '#D97706' : '#64748B'
  return {
    background: `conic-gradient(${accent} ${securityScore.value * 3.6}deg, rgba(148, 163, 184, 0.16) 0deg)`
  }
})

const securityHeadline = computed(() => {
  if (securityWarnings.value.length === 0) {
    return t('profile.security_all_clear_desc')
  }
  return securityWarnings.value[0].description
})

const realtimeStatusLabel = computed(() => {
  if (snapshotSyncing.value) {
    return t('profile.realtime_syncing')
  }
  if (streamStatus.value === 'live') {
    return t('profile.realtime_live')
  }
  if (streamStatus.value === 'reconnecting') {
    return t('profile.realtime_reconnecting')
  }
  return t('profile.realtime_paused')
})

const lastRealtimeSyncLabel = computed(() => {
  if (!snapshotUpdatedAt.value) {
    return t('profile.realtime_waiting')
  }
  return t('profile.realtime_updated_at', {
    time: formatDateTime(snapshotUpdatedAt.value, { dateStyle: 'short', timeStyle: 'short' })
  })
})

const activeSessionsHint = computed(() => {
  if (activeSessionCount.value === 0) {
    return t('profile.no_active_sessions_hint')
  }
  if (activeSessionCount.value === 1) {
    return t('profile.one_active_session_hint')
  }
  return t('profile.multi_active_sessions_hint', { count: activeSessionCount.value })
})

const securityWarnings = computed(() => {
  const warnings = []

  if (hasUnfamiliarIp.value) {
    warnings.push({
      key: 'ip-drift',
      severity: riskState.value.level === 'HIGH' ? 'critical' : 'review',
      icon: '!',
      title: t('profile.security_warning_ip_title'),
      description: t('profile.security_warning_ip_desc', { ip: latestKnownIp.value })
    })
  }

  if (!latestAccessDevice.value?.trusted && latestAccessDevice.value) {
    warnings.push({
      key: 'device-review',
      severity: riskState.value.level === 'HIGH' ? 'critical' : 'review',
      icon: '•',
      title: t('profile.security_warning_device_title'),
      description: t('profile.security_warning_device_desc', { device: latestAccessDevice.value.name })
    })
  }

  if (!isMfaEnabled.value) {
    warnings.push({
      key: 'mfa-off',
      severity: 'review',
      icon: '•',
      title: t('profile.security_warning_mfa_title'),
      description: t('profile.security_warning_mfa_desc')
    })
  }

  if (lastLoginAgeDays.value != null && lastLoginAgeDays.value > 30) {
    warnings.push({
      key: 'stale-login',
      severity: 'notice',
      icon: '⏱',
      title: t('profile.security_warning_stale_title'),
      description: t('profile.security_warning_stale_desc', { days: lastLoginAgeDays.value })
    })
  }

  if (hasTooManySessions.value) {
    warnings.push({
      key: 'many-sessions',
      severity: 'notice',
      icon: '↗',
      title: t('profile.security_warning_sessions_title'),
      description: t('profile.security_warning_sessions_desc', { count: activeSessionCount.value })
    })
  }

  if ((securityRisk.value?.trustedDeviceCount ?? trustedDevices.value.length) === 0) {
    warnings.push({
      key: 'no-trusted-devices',
      severity: 'review',
      icon: '•',
      title: t('profile.risk_reason_no_trusted_devices_title'),
      description: t('profile.risk_reason_no_trusted_devices_desc')
    })
  }

  return warnings
})

const riskReasons = computed(() => {
  return (securityRisk.value?.reasons || [])
    .map(mapRiskReason)
    .filter(Boolean)
})

const priorityActions = computed(() => {
  const mapped = (securityRisk.value?.recommendedActions || [])
    .map(mapPriorityAction)
    .filter(Boolean)

  return mapped.filter((action, index, all) => all.findIndex(candidate => candidate.key === action.key) === index).slice(0, 3)
})

const timelineEvents = computed(() => securityEvents.value.map(mapSecurityEvent))

const hasLiveEventPreview = computed(() => Boolean(liveEventPreview.value))

const newestLiveEventId = computed(() => liveEventPreview.value?.id || null)

const loginFreshness = computed(() => {
  if (lastLoginAgeDays.value == null) {
    return t('profile.login_unknown')
  }

  if (lastLoginAgeDays.value <= 0) {
    return t('profile.login_today')
  }
  if (lastLoginAgeDays.value === 1) {
    return t('profile.login_yesterday')
  }
  return t('profile.login_days_ago', { days: lastLoginAgeDays.value })
})

function formatDateTime(value, options = { dateStyle: 'medium', timeStyle: 'short' }) {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '-'
  return date.toLocaleString(localeTag.value, options)
}

function createEmptySocialLinks() {
  const links = {}
  for (const platform of socialPlatformOptions) {
    links[platform.key] = ''
  }
  return links
}

function normalizeHex(value, fallback) {
  return /^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$/.test(value || '') ? value.toUpperCase() : fallback
}

function createEmptyPremiumProfile() {
  return {
    bannerUrl: '',
    avatarFrame: 'gold',
    themePrimary: '#F59E0B',
    themeSecondary: '#FB7185',
    accentColor: '#0F172A',
    cardBackground: '#FFF7ED',
    shareCardStyle: 'spotlight',
    socialLinks: createEmptySocialLinks()
  }
}

function parsePremiumSocialLinks(rawJson) {
  const socialLinks = createEmptySocialLinks()
  if (!rawJson) {
    return socialLinks
  }

  try {
    const parsed = JSON.parse(rawJson)
    if (!Array.isArray(parsed)) {
      return socialLinks
    }
    for (const item of parsed) {
      const key = String(item?.platform || '').toLowerCase()
      const url = String(item?.url || '').trim()
      if (key && key in socialLinks && url) {
        socialLinks[key] = url
      }
    }
  } catch {
    return socialLinks
  }

  return socialLinks
}

function buildPremiumProfile(profile) {
  const defaults = createEmptyPremiumProfile()
  return {
    bannerUrl: profile.profileBannerUrl || '',
    avatarFrame: premiumFrameOptions.some(frame => frame.value === profile.premiumAvatarFrame)
      ? profile.premiumAvatarFrame
      : defaults.avatarFrame,
    themePrimary: normalizeHex(profile.premiumThemePrimary, defaults.themePrimary),
    themeSecondary: normalizeHex(profile.premiumThemeSecondary, defaults.themeSecondary),
    accentColor: normalizeHex(profile.premiumThemeAccent, defaults.accentColor),
    cardBackground: normalizeHex(profile.premiumPostCardBackground, defaults.cardBackground),
    shareCardStyle: shareCardPresets.some(preset => preset.value === profile.premiumShareCardStyle)
      ? profile.premiumShareCardStyle
      : defaults.shareCardStyle,
    socialLinks: parsePremiumSocialLinks(profile.profileSocialLinksJson)
  }
}

function createEditForm(sourceUser = user.value) {
  const profile = sourceUser?.premiumProfile || createEmptyPremiumProfile()
  return {
    username: sourceUser?.name || '',
    bio: sourceUser?.bio || '',
    avatarFile: null,
    avatarPreview: '',
    bannerFile: null,
    bannerPreview: '',
    removeBanner: false,
    avatarFrame: profile.avatarFrame,
    themePrimary: profile.themePrimary,
    themeSecondary: profile.themeSecondary,
    accentColor: profile.accentColor,
    cardBackground: profile.cardBackground,
    shareCardStyle: profile.shareCardStyle,
    socialLinks: { ...createEmptySocialLinks(), ...profile.socialLinks }
  }
}

function applyThemePreset(preset) {
  editForm.value.avatarFrame = preset.avatarFrame
  editForm.value.themePrimary = preset.themePrimary
  editForm.value.themeSecondary = preset.themeSecondary
  editForm.value.accentColor = preset.accentColor
  editForm.value.cardBackground = preset.cardBackground
}

async function copyShareCaption() {
  try {
    await navigator.clipboard.writeText(shareCaption.value)
    toast.success('Đã sao chép caption share.')
  } catch {
    toast.info(shareCaption.value)
  }
}

function syncAuthProfileSnapshot() {
  if (!isOwnProfile.value || !authStore.user) {
    return
  }

  authStore.user = {
    ...authStore.user,
    name: user.value.name,
    username: user.value.name,
    avatar: user.value.avatar,
    isPremium: user.value.isPremium
  }
  localStorage.setItem('user', JSON.stringify(authStore.user))
}

async function shareProfile() {
  const shareUrl = shareProfileUrl.value

  try {
    if (navigator.share) {
      await navigator.share({
        title: `${user.value.name} | GoMet`,
        text: shareCaption.value,
        url: shareUrl
      })
      return
    }

    await navigator.clipboard.writeText(shareCaption.value)
    toast.success('Đã sao chép caption và link hồ sơ.')
  } catch {
    toast.info(shareUrl)
  }
}

function slugifyFilename(value) {
  return String(value || 'profile')
    .toLowerCase()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/[^a-z0-9]+/g, '-')
    .replace(/^-+|-+$/g, '') || 'profile'
}

async function exportShareCardImage() {
  if (!shareCardExportRef.value) {
    toast.error('Không tìm thấy preview card để xuất ảnh.')
    return
  }

  try {
    const dataUrl = await toPng(shareCardExportRef.value, {
      cacheBust: true,
      pixelRatio: Math.max(2, Math.min(window.devicePixelRatio || 2, 3)),
      backgroundColor: '#ffffff'
    })

    const link = document.createElement('a')
    link.href = dataUrl
    link.download = `gomet-${slugifyFilename(editForm.value.username || user.value.name)}-share-card.png`
    link.click()
    toast.success('Đã xuất ảnh share card.')
  } catch {
    toast.error('Không thể xuất ảnh share card lúc này.')
  }
}

function formatDateOnly(value) {
  return formatDateTime(value, { year: 'numeric', month: 'long', day: 'numeric' })
}

function parseEventMeta(eventMetaJson) {
  if (!eventMetaJson) return {}
  try {
    return JSON.parse(eventMetaJson)
  } catch {
    return {}
  }
}

function mapSecurityEvent(event) {
  const meta = parseEventMeta(event.eventMetaJson)
  const base = {
    id: event.id,
    time: formatDateTime(event.createdAt, { dateStyle: 'short', timeStyle: 'short' }),
    ip: event.ip || '-',
    severity: 'low',
    icon: '•',
    title: event.eventType,
    description: t('profile.event_generic_desc')
  }

  switch (event.eventType) {
    case 'MFA_ENABLED':
      return { ...base, severity: 'low', icon: '🛡', title: t('profile.event_mfa_enabled_title'), description: t('profile.event_mfa_enabled_desc') }
    case 'MFA_DISABLED':
      return { ...base, severity: 'high', icon: '⚠', title: t('profile.event_mfa_disabled_title'), description: t('profile.event_mfa_disabled_desc') }
    case 'DEVICE_TRUSTED':
      return { ...base, severity: 'low', icon: '✓', title: t('profile.event_device_trusted_title'), description: t('profile.event_device_trusted_desc') }
    case 'DEVICE_REVOKED':
      return { ...base, severity: 'medium', icon: '✕', title: t('profile.event_device_revoked_title'), description: t('profile.event_device_revoked_desc', { id: meta.deviceDbId || '-' }) }
    case 'DEVICE_REVOKE_ALL':
      return { ...base, severity: 'medium', icon: '✕', title: t('profile.event_device_revoke_all_title'), description: t('profile.event_device_revoke_all_desc') }
    case 'SESSION_REVOKE':
      return { ...base, severity: 'medium', icon: '↗', title: t('profile.event_session_revoked_title'), description: t('profile.event_session_revoked_desc', { id: meta.sessionId || '-' }) }
    case 'SESSION_REVOKE_ALL':
      return { ...base, severity: 'medium', icon: '↗', title: t('profile.event_session_revoke_all_title'), description: t('profile.event_session_revoke_all_desc') }
    case 'LOGIN_SUSPICIOUS':
      return { ...base, severity: 'high', icon: '!', title: t('profile.event_login_suspicious_title'), description: t('profile.event_login_suspicious_desc') }
    case 'LOGIN_SUCCESS':
      return { ...base, severity: 'low', icon: '→', title: t('profile.event_login_success_title'), description: t('profile.event_login_success_desc') }
    case 'MFA_BACKUP_CODE_USED':
      return { ...base, severity: 'medium', icon: '🔐', title: t('profile.event_backup_code_title'), description: t('profile.event_backup_code_desc') }
    case 'PASSWORD_CHANGE':
      return { ...base, severity: 'low', icon: '🔑', title: t('profile.event_password_change_title'), description: t('profile.event_password_change_desc') }
    case 'REFRESH_TOKEN_REUSE_DETECTED':
      return { ...base, severity: 'high', icon: '⚠', title: t('profile.event_refresh_reuse_title'), description: t('profile.event_refresh_reuse_desc') }
    default:
      return base
  }
}

function mapRiskReason(reasonKey) {
  switch (reasonKey) {
    case 'trusted_device':
      return { key: reasonKey, icon: '✓', tone: 'good', title: t('profile.risk_reason_trusted_device_title'), description: t('profile.risk_reason_trusted_device_desc') }
    case 'no_device_history':
      return { key: reasonKey, icon: '◎', tone: 'review', title: t('profile.risk_reason_no_device_history_title'), description: t('profile.risk_reason_no_device_history_desc') }
    case 'same_device_as_last':
      return { key: reasonKey, icon: '↺', tone: 'good', title: t('profile.risk_reason_same_device_as_last_title'), description: t('profile.risk_reason_same_device_as_last_desc') }
    case 'ip_changed':
      return { key: reasonKey, icon: '!', tone: 'critical', title: t('profile.risk_reason_ip_changed_title'), description: t('profile.risk_reason_ip_changed_desc') }
    case 'missing_device_fingerprint':
      return { key: reasonKey, icon: '◌', tone: 'review', title: t('profile.risk_reason_missing_device_fingerprint_title'), description: t('profile.risk_reason_missing_device_fingerprint_desc') }
    case 'no_trusted_devices':
      return { key: reasonKey, icon: '•', tone: 'review', title: t('profile.risk_reason_no_trusted_devices_title'), description: t('profile.risk_reason_no_trusted_devices_desc') }
    case 'many_active_sessions':
      return { key: reasonKey, icon: '↗', tone: 'review', title: t('profile.risk_reason_many_active_sessions_title'), description: t('profile.risk_reason_many_active_sessions_desc', { count: activeSessionCount.value }) }
    case 'mfa_disabled':
      return { key: reasonKey, icon: '🔓', tone: 'critical', title: t('profile.risk_reason_mfa_disabled_title'), description: t('profile.risk_reason_mfa_disabled_desc') }
    case 'verification_required':
      return { key: reasonKey, icon: '⚠', tone: 'critical', title: t('profile.risk_reason_verification_required_title'), description: t('profile.risk_reason_verification_required_desc') }
    default:
      return null
  }
}

function mapPriorityAction(actionKey) {
  switch (actionKey) {
    case 'enable_mfa':
      return { key: actionKey, kind: 'route', to: '/settings/security', tone: 'primary', label: t('profile.priority_enable_mfa_label'), note: t('profile.priority_enable_mfa_note') }
    case 'review_latest_device':
      if (latestTrustedDevice.value?.id) {
        return { key: actionKey, kind: 'button', tone: 'warning', label: t('profile.priority_review_latest_device_label'), note: t('profile.priority_review_latest_device_note') }
      }
      return { key: actionKey, kind: 'route', to: '/settings/security', tone: 'primary', label: t('profile.priority_review_recent_ip_label'), note: t('profile.priority_review_recent_ip_note') }
    case 'review_recent_ip':
      return { key: actionKey, kind: 'route', to: '/settings/security', tone: 'warning', label: t('profile.priority_review_recent_ip_label'), note: t('profile.priority_review_recent_ip_note') }
    case 'review_sessions':
      if (suspiciousSessions.value.length) {
        return { key: actionKey, kind: 'button', tone: 'danger', label: t('profile.priority_review_sessions_label'), note: t('profile.priority_review_sessions_note', { count: suspiciousSessions.value.length }) }
      }
      return { key: actionKey, kind: 'route', to: '/settings/security', tone: 'primary', label: t('profile.priority_review_sessions_label'), note: t('profile.priority_review_sessions_note', { count: activeSessionCount.value }) }
    case 'add_trusted_device':
      return { key: actionKey, kind: 'route', to: '/settings/security', tone: 'primary', label: t('profile.priority_add_trusted_device_label'), note: t('profile.priority_add_trusted_device_note') }
    default:
      return null
  }
}

function clearLiveEventPreview() {
  if (liveEventPreviewTimer) {
    clearTimeout(liveEventPreviewTimer)
    liveEventPreviewTimer = null
  }
  liveEventPreview.value = null
}

function showLiveEventPreview(event) {
  clearLiveEventPreview()
  liveEventPreview.value = mapSecurityEvent(event)
  liveEventPreviewTimer = window.setTimeout(() => {
    liveEventPreview.value = null
    liveEventPreviewTimer = null
  }, 8000)
}

function openDangerConfirm(actionKey) {
  if (actionKey === 'review_latest_device' && latestTrustedDevice.value?.id) {
    dangerConfirm.value = {
      open: true,
      action: actionKey,
      title: t('profile.revoke_latest_device'),
      description: t('profile.priority_review_latest_device_note'),
      highlights: [latestTrustedDevice.value.deviceName || t('profile.unknown_device')],
      confirmLabel: t('profile.revoke_latest_device'),
      loading: false
    }
    return
  }

  if (actionKey === 'review_sessions' && suspiciousSessions.value.length) {
    dangerConfirm.value = {
      open: true,
      action: actionKey,
      title: t('profile.revoke_suspicious_sessions'),
      description: t('profile.priority_review_sessions_note', { count: suspiciousSessions.value.length }),
      highlights: suspiciousSessions.value.slice(0, 3).map(session => session.deviceName || session.userAgent || session.ip || t('profile.unknown_device')),
      confirmLabel: t('profile.revoke_suspicious_sessions'),
      loading: false
    }
  }
}

function closeDangerConfirm() {
  dangerConfirm.value = { open: false, action: '', title: '', description: '', highlights: [], confirmLabel: '', loading: false }
}

async function confirmDangerAction() {
  if (!dangerConfirm.value.action) {
    return
  }

  dangerConfirm.value = { ...dangerConfirm.value, loading: true }
  try {
    if (dangerConfirm.value.action === 'review_latest_device') {
      await revokeLatestTrustedDeviceAction()
    } else if (dangerConfirm.value.action === 'review_sessions') {
      await signOutSuspiciousSessionsAction()
    }
    closeDangerConfirm()
  } catch {
    dangerConfirm.value = { ...dangerConfirm.value, loading: false }
  }
}

async function refreshSecurityRealtime() {
  if (!isOwnProfile.value) {
    clearSecurityState()
    return
  }

  await refreshSecurityState()
}

function stopSecurityRealtime() {
  stopLiveStream()
  clearLiveEventPreview()
}

function startSecurityRealtime() {
  if (!showAdvancedInfo.value || !isOwnProfile.value) {
    return
  }

  startLiveStream()
}

function runPriorityAction(actionKey) {
  if (actionKey === 'review_latest_device') {
    void revokeLatestTrustedDeviceAction()
    return
  }

  if (actionKey === 'review_sessions') {
    void signOutSuspiciousSessionsAction()
    return
  }

  void refreshSecurityRealtime()
}

function handleVisibilityChange() {
  if (document.visibilityState === 'visible' && showAdvancedInfo.value && isOwnProfile.value) {
    void refreshSecurityRealtime()
    startSecurityRealtime()
  }
}

async function revokeLatestTrustedDeviceAction() {
  if (!latestTrustedDevice.value?.id) {
    return
  }

  try {
    const revokedId = latestTrustedDevice.value.id
    await revokeSecurityDevice(revokedId)
    toast.success(t('profile.device_revoke_success'))
  } catch {
    toast.error(t('profile.security_action_failed'))
    throw new Error('DEVICE_REVOKE_FAILED')
  }
}

async function signOutSuspiciousSessionsAction() {
  const targets = suspiciousSessions.value.map(session => session.id).filter(Boolean)
  if (!targets.length) {
    return
  }

  try {
    const results = await Promise.allSettled(targets.map(id => revokeSecuritySession(id)))
    const revokedCount = results.filter(result => result.status === 'fulfilled').length
    if (!revokedCount) {
      throw new Error('NO_REVOKE')
    }
    toast.success(t('profile.sessions_revoke_success', { count: revokedCount }))
  } catch {
    toast.error(t('profile.security_action_failed'))
    throw new Error('SESSION_REVOKE_FAILED')
  }
}

async function loadProfile() {
  // Use route param id if present, otherwise use logged-in user
  const targetId = route.params.id || authStore.user?.accountID
  if (!targetId) return
  try {
    const [profile, userPosts, userAch] = await Promise.all([
      getUserProfile(targetId),
      getPostsByUser(targetId),
      getUserAchievements(targetId)
    ])
    const premiumConfig = buildPremiumProfile(profile)
    user.value = {
      name:       profile.username || 'Chef',
      handle:     (profile.username || 'chef').toLowerCase().replace(/\s+/g, '_'),
      email:      profile.email || authStore.user?.email || '',
      avatar:     profile.avatar || `https://ui-avatars.com/api/?name=${encodeURIComponent(profile.username||'G')}&background=EA580C&color=fff`,
      bio:        profile.bio || '',
      isAdmin:    profile.isAdmin === 1 || profile.isAdmin === true,
      isPremium:  profile.isPremium === 1 || profile.isPremium === true,
      postsCount: profile.postCount || 0,
      followers:  profile.followerCount > 999
                    ? `${(profile.followerCount/1000).toFixed(1)}k`
                    : `${profile.followerCount || 0}`,
      following:  profile.followingCount || 0,
      point:      profile.point || 0,
      totalViews: profile.totalViews > 999
                    ? `${(profile.totalViews/1000).toFixed(1)}k`
                    : `${profile.totalViews || 0}`,
      createdAt:  profile.createdAt
                    ? formatDateOnly(profile.createdAt)
                    : '',
      lastLoginAtRaw: profile.lastLoginAt || '',
      lastLoginAt: profile.lastLoginAt
                    ? formatDateTime(profile.lastLoginAt, { dateStyle: 'short', timeStyle: 'short' })
                    : '',
      lastLoginIp:  profile.lastLoginIp || '',
      mfaEnabled:   profile.mfaEnabled || 0,
      premiumProfile: premiumConfig
    }
    syncAuthProfileSnapshot()
    allPosts.value = userPosts.map(normalizePost)
    postsLoading.value = false
    const rawLikes = allPosts.value.reduce((s, p) => s + (p.likes || 0), 0)
    user.value.totalLikes = rawLikes > 999999
      ? `${(rawLikes / 1000000).toFixed(1)}M`
      : rawLikes > 999
        ? `${(rawLikes / 1000).toFixed(1)}k`
        : `${rawLikes}`
    achievements.value = userAch || []
    if (isOwnProfile.value) {
      certificatesLoading.value = true
      try {
        certificates.value = await getMyCertificates()
      } catch {
        certificates.value = []
      } finally {
        certificatesLoading.value = false
      }

      await refreshSecurityRealtime()
    } else {
      clearSecurityState()
    }
    // Check follow status if viewing someone else's profile
    const myId = authStore.user?.accountID || authStore.user?.id
    if (!isOwnProfile.value && myId) {
      try {
        const status = await checkFollow(myId, targetId)
        isFollowing.value = status === true || status?.following === true
      } catch { isFollowing.value = false }
    }
  } catch (err) {
    console.warn('ProfilePage: API error', err)
    postsLoading.value = false
  }
}

async function toggleFollow() {
  const myId = authStore.user?.accountID || authStore.user?.id
  const targetId = route.params.id
  if (!myId || !targetId || followLoading.value) return
  followLoading.value = true
  try {
    if (isFollowing.value) {
      await unfollow(myId, targetId)
      isFollowing.value = false
      if (user.value.followers) {
        const n = parseInt(String(user.value.followers).replace('k', '')) || 0
        user.value.followers = String(Math.max(0, n - 1))
      }
    } else {
      await follow(myId, targetId)
      isFollowing.value = true
      const n = parseInt(String(user.value.followers).replace('k', '')) || 0
      user.value.followers = String(n + 1)
    }
  } catch (err) {
    toast.error('Action failed, please try again.')
  } finally {
    followLoading.value = false
  }
}

function openEditModal() {
  editForm.value = createEditForm(user.value)
  showEditModal.value = true
}

function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  editForm.value.avatarFile = file
  editForm.value.avatarPreview = URL.createObjectURL(file)
}

function onBannerChange(e) {
  const file = e.target.files[0]
  if (!file) return
  editForm.value.bannerFile = file
  editForm.value.bannerPreview = URL.createObjectURL(file)
  editForm.value.removeBanner = false
}

function clearBanner() {
  editForm.value.bannerFile = null
  editForm.value.bannerPreview = ''
  editForm.value.removeBanner = true
}

async function saveProfile() {
  if (editSaving.value) return
  const targetId = route.params.id || authStore.user?.accountID
  if (!targetId) return
  editSaving.value = true
  try {
    const payload = { username: editForm.value.username, bio: editForm.value.bio }
    // Upload new avatar if user selected a file
    if (editForm.value.avatarFile) {
      try {
        const avatarUrl = await uploadMedia(editForm.value.avatarFile)
        payload.avatar = avatarUrl
      } catch { /* avatar upload failed — skip updating avatar */ }
    }

    if (user.value.isPremium) {
      let bannerUrl = editForm.value.removeBanner ? null : premiumProfile.value.bannerUrl || null
      if (editForm.value.bannerFile) {
        try {
          bannerUrl = await uploadMedia(editForm.value.bannerFile)
        } catch { /* banner upload failed — keep previous banner */ }
      }

      payload.profileBannerUrl = bannerUrl
      payload.premiumAvatarFrame = editForm.value.avatarFrame
      payload.premiumThemePrimary = normalizeHex(editForm.value.themePrimary, '#F59E0B')
      payload.premiumThemeSecondary = normalizeHex(editForm.value.themeSecondary, '#FB7185')
      payload.premiumThemeAccent = normalizeHex(editForm.value.accentColor, '#0F172A')
      payload.premiumPostCardBackground = normalizeHex(editForm.value.cardBackground, '#FFF7ED')
      payload.premiumShareCardStyle = editForm.value.shareCardStyle
      payload.profileSocialLinksJson = JSON.stringify(
        socialPlatformOptions
          .map(platform => ({
            platform: platform.key,
            url: String(editForm.value.socialLinks?.[platform.key] || '').trim()
          }))
          .filter(item => item.url)
      )
    }

    await updateUserProfile(targetId, payload)
    // Refresh profile
    await loadProfile()
    showEditModal.value = false
    toast.success('Profile đã được cập nhật.')
  } catch (err) {
    toast.error(err?.response?.data?.message || 'Lưu hồ sơ thất bại. Vui lòng thử lại.')
  } finally {
    editSaving.value = false
  }
}

watch(showAdvancedInfo, visible => {
  if (visible && isOwnProfile.value) {
    void refreshSecurityRealtime()
    startSecurityRealtime()
    return
  }

  stopSecurityRealtime()
  clearLiveEventPreview()
})

onMounted(() => {
  void loadProfile()
  document.addEventListener('visibilitychange', handleVisibilityChange)
})

onUnmounted(() => {
  stopSecurityRealtime()
  document.removeEventListener('visibilitychange', handleVisibilityChange)
})
</script>

<style scoped lang="scss" src="./ProfilePage.scss"></style>
