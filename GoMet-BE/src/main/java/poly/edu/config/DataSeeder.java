package poly.edu.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import poly.edu.dao.AchievementDAO;
import poly.edu.entity.Achievement;

import java.util.List;

/**
 * Seeds initial reference data (achievements catalogue) on first startup.
 * Runs after application context is ready. Skips seeding if data already exists.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AchievementDAO achievementDAO;

    @Override
    public void run(String... args) {
        seedAchievements();
    }

    // ── Achievement catalogue ────────────────────────────────────────────
    private void seedAchievements() {
        if (achievementDAO.count() > 0) {
            log.info("DataSeeder: Achievements already seeded — skipping.");
            return;
        }

        List<Achievement> achievements = List.of(
            Achievement.builder()
                .achievementName("First Recipe")
                .description("Published your very first recipe on GoMet.")
                .icon("chef-hat")
                .build(),
            Achievement.builder()
                .achievementName("Fan Favourite")
                .description("Your recipe has been saved by 50 different chefs.")
                .icon("heart")
                .build(),
            Achievement.builder()
                .achievementName("Rising Star")
                .description("Gained 10 followers in the community.")
                .icon("star")
                .build(),
            Achievement.builder()
                .achievementName("Social Butterfly")
                .description("Followed 20 chefs in the community.")
                .icon("🦋")
                .build(),
            Achievement.builder()
                .achievementName("Gourmet Master")
                .description("Published 10 approved recipes.")
                .icon("chef-hat")
                .build(),
            Achievement.builder()
                .achievementName("Culinary Legend")
                .description("Reached the top 3 on the leaderboard.")
                .icon("trophy")
                .build(),
            Achievement.builder()
                .achievementName("Recipe Collector")
                .description("Saved 20 recipes to your collection.")
                .icon("📚")
                .build(),
            Achievement.builder()
                .achievementName("Comment Guru")
                .description("Left 30 comments on community recipes.")
                .icon("message-circle")
                .build(),
            Achievement.builder()
                .achievementName("Event Champion")
                .description("Won a GoMet cooking event.")
                .icon("🥇")
                .build(),
            Achievement.builder()
                .achievementName("GoMet VIP")
                .description("Became a premium GoMet member.")
                .icon("💎")
                .build(),
            Achievement.builder()
                .achievementName("Viral Dish")
                .description("A recipe reached 1000 views.")
                .icon("flame")
                .build(),
            Achievement.builder()
                .achievementName("Perfectionist")
                .description("Received a 5-star rating on a recipe.")
                .icon("sparkles")
                .build()
        );

        achievementDAO.saveAll(achievements);
        log.info("DataSeeder: Seeded {} achievements.", achievements.size());
    }
}
