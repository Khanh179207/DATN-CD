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