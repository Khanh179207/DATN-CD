IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'profile_banner_url'
)
    ALTER TABLE Account ADD profile_banner_url NVARCHAR(1000) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_avatar_frame'
)
    ALTER TABLE Account ADD premium_avatar_frame NVARCHAR(32) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_theme_primary'
)
    ALTER TABLE Account ADD premium_theme_primary NVARCHAR(32) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_theme_secondary'
)
    ALTER TABLE Account ADD premium_theme_secondary NVARCHAR(32) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_theme_accent'
)
    ALTER TABLE Account ADD premium_theme_accent NVARCHAR(32) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_post_card_background'
)
    ALTER TABLE Account ADD premium_post_card_background NVARCHAR(32) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'profile_social_links_json'
)
    ALTER TABLE Account ADD profile_social_links_json NVARCHAR(MAX) NULL;

IF NOT EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premium_share_card_style'
)
    ALTER TABLE Account ADD premium_share_card_style NVARCHAR(32) NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'profileBannerUrl'
)
    UPDATE Account
    SET profile_banner_url = COALESCE(profile_banner_url, profileBannerUrl)
    WHERE profileBannerUrl IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumAvatarFrame'
)
    UPDATE Account
    SET premium_avatar_frame = COALESCE(premium_avatar_frame, premiumAvatarFrame)
    WHERE premiumAvatarFrame IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumThemePrimary'
)
    UPDATE Account
    SET premium_theme_primary = COALESCE(premium_theme_primary, premiumThemePrimary)
    WHERE premiumThemePrimary IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumThemeSecondary'
)
    UPDATE Account
    SET premium_theme_secondary = COALESCE(premium_theme_secondary, premiumThemeSecondary)
    WHERE premiumThemeSecondary IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumThemeAccent'
)
    UPDATE Account
    SET premium_theme_accent = COALESCE(premium_theme_accent, premiumThemeAccent)
    WHERE premiumThemeAccent IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumPostCardBackground'
)
    UPDATE Account
    SET premium_post_card_background = COALESCE(premium_post_card_background, premiumPostCardBackground)
    WHERE premiumPostCardBackground IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'profileSocialLinksJson'
)
    UPDATE Account
    SET profile_social_links_json = COALESCE(profile_social_links_json, profileSocialLinksJson)
    WHERE profileSocialLinksJson IS NOT NULL;

IF EXISTS (
    SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_NAME = 'Account' AND COLUMN_NAME = 'premiumShareCardStyle'
)
    UPDATE Account
    SET premium_share_card_style = COALESCE(premium_share_card_style, premiumShareCardStyle)
    WHERE premiumShareCardStyle IS NOT NULL;