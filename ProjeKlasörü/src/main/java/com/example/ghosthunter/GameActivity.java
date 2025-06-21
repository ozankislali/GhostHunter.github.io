package com.example.ghosthunter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {

    private FrameLayout ghostContainer;
    private TextView scoreText;
    private LinearLayout livesContainer;
    private int score = 0;
    private int lives = 5;
    private Random random = new Random();
    private int level = 1;
    private int ghostsCaught = 0;
    private ScheduledExecutorService scheduler;
    private boolean isPaused = false;

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize UI components
        ghostContainer = findViewById(R.id.ghostContainer);
        scoreText = findViewById(R.id.scoreText);
        livesContainer = findViewById(R.id.livesContainer);

        // Initialize lives display
        updateLivesDisplay();

        // Initialize game
        scoreText.setText("Score: " + score + " | Level: " + level);

        // Start ghost spawning
        scheduler = Executors.newScheduledThreadPool(1);
        startGhostSpawning();

        mediaPlayer = MediaPlayer.create(this, R.raw.music2);
        mediaPlayer.setLooping(true); // Müziği sürekli tekrar etmesi için ayarla
        mediaPlayer.start();
    }

    private void startGhostSpawning() {
        // Level'a göre spawn hızını artır
        int spawnDelay = Math.max(1, 3 - (level / 3)); // Her 3 level'de 1 saniye azalt, min 1 saniye

        scheduler.scheduleAtFixedRate(() -> {
            if (!isPaused) {
                spawnGhost();
            }
        }, 1, spawnDelay, TimeUnit.SECONDS);

        // Yüksek level'larda ek hayalet spawn'u
        if (level > 5) {
            scheduler.scheduleAtFixedRate(() -> {
                if (!isPaused) {
                    spawnGhost();
                }
            }, 2, spawnDelay + 1, TimeUnit.SECONDS);
        }
        if (level > 10) {
            scheduler.scheduleAtFixedRate(() -> {
                if (!isPaused) {
                    spawnGhost();
                }
            }, 3, spawnDelay + 2, TimeUnit.SECONDS);
        }
    }

    private void updateLivesDisplay() {
        livesContainer.removeAllViews();

        for (int i = 0; i < lives; i++) {
            ImageView heartView = new ImageView(this);
            heartView.setImageResource(android.R.drawable.btn_star_big_on);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
            params.setMargins(5, 0, 5, 0);
            heartView.setLayoutParams(params);

            livesContainer.addView(heartView);
        }
    }

    private void animateLivesOnLoss() {
        for (int i = 0; i < livesContainer.getChildCount(); i++) {
            ImageView heart = (ImageView) livesContainer.getChildAt(i);

            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(heart, "scaleX", 1.0f, 1.5f);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(heart, "scaleY", 1.0f, 1.5f);
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(heart, "scaleX", 1.5f, 1.0f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(heart, "scaleY", 1.5f, 1.0f);

            scaleUpX.setDuration(300);
            scaleUpY.setDuration(300);
            scaleDownX.setDuration(300);
            scaleDownY.setDuration(300);

            scaleUpX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleUpY.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleDownX.setInterpolator(new AccelerateDecelerateInterpolator());
            scaleDownY.setInterpolator(new AccelerateDecelerateInterpolator());

            int delay = i * 100;

            new Handler().postDelayed(() -> {
                scaleUpX.start();
                scaleUpY.start();

                scaleUpX.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scaleDownX.start();
                        scaleDownY.start();
                    }
                });
            }, delay);
        }
    }

    private void loseLife() {
        lives--;
        updateLivesDisplay();
        animateLivesOnLoss();

        if (lives <= 0) {
            showGameOverDialog();
        } else {
            //Toast.makeText(this, "Can kaybettiniz! Kalan can: " + lives, Toast.LENGTH_SHORT).show();
        }
    }

    private void showGameOverDialog() {
        if (scheduler != null) {
            scheduler.shutdown();
        }

        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Game Over!")
                    .setMessage("Tüm canlarınızı kaybettiniz!\nLevel: " + level + "\nFinal Skorunuz: " + score)
                    .setCancelable(false)
                    .setPositiveButton("Ana Menüye Dön", (dialog, which) -> {
                        Intent intent = new Intent(GameActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Tekrar Oyna", (dialog, which) -> {
                        restartGame();
                    })
                    .show();
        });
    }

    private void restartGame() {
        score = 0;
        lives = 3;
        level = 1;
        ghostsCaught = 0;
        scoreText.setText("Score: " + score + " | Level: " + level);
        updateLivesDisplay();

        ghostContainer.removeAllViews();

        if (scheduler != null) {
            scheduler.shutdown();
        }
        scheduler = Executors.newScheduledThreadPool(1);
        startGhostSpawning();
    }

    private void checkLevelUp() {
        // Her 5 hayalet yakalandığında level up
        if (ghostsCaught > 0 && ghostsCaught % 5 == 0) {
            level++;

            // Level artırırken spawning'i yeniden başlat
            if (scheduler != null) {
                scheduler.shutdown();
            }

            // Oyunu duraklat
            isPaused = true;

            // Level UP popup göster
            showLevelUpPopup();

            // 3 saniye sonra oyunu devam ettir
            new Handler().postDelayed(() -> {
                isPaused = false;
                scheduler = Executors.newScheduledThreadPool(1);
                startGhostSpawning();
                scoreText.setText("Score: " + score + " | Level: " + level);
            }, 3000);
        }
    }

    private void showLevelUpPopup() {
        runOnUiThread(() -> {
            // Popup için View oluştur
            View popupView = getLayoutInflater().inflate(R.layout.level_up_popup, null);

            // Level bilgisini ayarla
            TextView levelText = popupView.findViewById(R.id.levelText);
            levelText.setText("LEVEL " + level + "!");

            // PopupWindow oluştur
            final PopupWindow popupWindow = new PopupWindow(
                    popupView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    true
            );

            // Popup'u ekranın ortasında göster
            popupWindow.showAtLocation(ghostContainer, Gravity.CENTER, 0, 0);

            // 2.5 saniye sonra popup'u kapat
            new Handler().postDelayed(popupWindow::dismiss, 2500);
        });
    }

    private void spawnGhost() {
        if (lives <= 0 || isPaused) {
            return;
        }

        runOnUiThread(() -> {
            int ghostType = random.nextInt(3) + 1;
            ImageView ghostImage = new ImageView(this);

            // Level'a göre zorlaştırılmış özellikler
            int resourceId, basePoints, durationMs;
            switch (ghostType) {
                case 1:
                    resourceId = R.drawable.ghost_type1;
                    basePoints = 20;
                    durationMs = Math.max(500, 1500 - (level * 50)); // Level arttıkça daha hızlı
                    break;
                case 2:
                    resourceId = R.drawable.ghost_type2;
                    basePoints = 10;
                    durationMs = Math.max(800, 3000 - (level * 100));
                    break;
                default:
                    resourceId = R.drawable.ghost_type3;
                    basePoints = 40;
                    durationMs = Math.max(300, 750 - (level * 25)); // En hızlı hayalet
                    break;
            }

            ghostImage.setImageResource(resourceId);

            int screenWidth = ghostContainer.getWidth();
            int screenHeight = ghostContainer.getHeight();

            // Level'a göre daha küçük başlangıç boyutu
            int startSize = Math.max(30, Math.min(screenWidth, screenHeight) / (15 + level)); // Level arttıkça daha küçük
            int endSize = Math.max(80, Math.min(screenWidth, screenHeight) / (5 + level/3)); // Daha küçük bitiş boyutu

            // Rastgele pozisyon
            int leftMargin = random.nextInt(Math.max(1, screenWidth - endSize));
            int topMargin = random.nextInt(Math.max(1, screenHeight - endSize));

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(startSize, startSize);
            params.leftMargin = leftMargin;
            params.topMargin = topMargin;
            ghostImage.setLayoutParams(params);

            // Her hayalet hareket eder, level'a göre hareket türü ve hızı değişir
            int movementType = getMovementType();

            GhostWithSize ghost = new GhostWithSize(ghostImage, basePoints, durationMs,
                    "ghost_type_" + ghostType, startSize, endSize, movementType);

            ghostImage.setOnClickListener(v -> {
                int currentSize = ghostImage.getLayoutParams().width;
                int points = calculatePoints(basePoints, currentSize, startSize, endSize);
                catchGhost(ghost, points);

                // Puan numarasını göster
                showFloatingScore(points, ghostImage.getX() + ghostImage.getWidth()/2,
                        ghostImage.getY());
            });

            ghostContainer.addView(ghostImage);

            // Büyüme animasyonu
            animateGhostGrowth(ghostImage, startSize, endSize, durationMs);

            // Hareket animasyonu (tüm hayaletler hareket eder)
            animateGhostMovement(ghostImage, movementType, durationMs, screenWidth, screenHeight);

            // Kaybolma animasyonu
            new Handler().postDelayed(() -> {
                if (ghostImage.getParent() != null && !isPaused) {
                    // Kaybolurken alpha animasyonu
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(ghostImage, "alpha", 1.0f, 0.0f);
                    fadeOut.setDuration(200);
                    fadeOut.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (ghostImage.getParent() != null) {
                                ((ViewGroup) ghostImage.getParent()).removeView(ghostImage);
                                loseLife();
                            }
                        }
                    });
                    fadeOut.start();
                }
            }, durationMs);
        });
    }

    private void showFloatingScore(int points, float x, float y) {
        // Yeni TextView oluştur
        TextView scoreView = new TextView(this);
        scoreView.setText("+" + points);
        scoreView.setTextSize(18);
        scoreView.setTextColor(Color.YELLOW);
        scoreView.setShadowLayer(3, 1, 1, Color.BLACK);

        // FrameLayout parametreleri
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int)x - 30; // x pozisyonu (ortadan biraz sola)
        params.topMargin = (int)y - 30;  // y pozisyonu (biraz yukarıda)
        scoreView.setLayoutParams(params);

        // Container'a ekle
        ghostContainer.addView(scoreView);

        // Yukarı doğru yükselen ve kaybolan animasyon
        ObjectAnimator moveAnim = ObjectAnimator.ofFloat(scoreView, "translationY", 0, -100);
        ObjectAnimator fadeAnim = ObjectAnimator.ofFloat(scoreView, "alpha", 1.0f, 0.0f);

        moveAnim.setDuration(500);
        fadeAnim.setDuration(500);

        moveAnim.start();
        fadeAnim.start();

        // 0.5 saniye sonra kaldır
        new Handler().postDelayed(() -> {
            if (scoreView.getParent() != null) {
                ((ViewGroup) scoreView.getParent()).removeView(scoreView);
            }
        }, 500);
    }

    private int getMovementType() {
        // Level'a göre hareket türü seçimi
        if (level <= 2) {
            // Level 1-2: Sadece yavaş yatay/dikey hareket
            return random.nextInt(3); // 0: yavaş yatay, 1: yavaş dikey, 2: minimal zigzag
        } else if (level <= 5) {
            // Level 3-5: Orta hızda hareket, tüm türler
            return random.nextInt(4); // 0-3: tüm hareket türleri
        } else if (level <= 10) {
            // Level 6-10: Hızlı hareket + dairesel
            return random.nextInt(5); // 0-4: tüm hareket türleri + dairesel
        } else {
            // Level 10+: Kaotik hareket + teleport
            return random.nextInt(6); // 0-5: tüm hareket türleri + teleport
        }
    }

    private void animateGhostMovement(ImageView ghostImage, int movementType, int duration, int screenWidth, int screenHeight) {
        // Level'a göre hareket hızı çarpanı (düşük level = yavaş hareket)
        float speedMultiplier = Math.min(2.0f, 0.3f + (level * 0.15f)); // Level 1: 0.45x, Level 10+: 2x

        switch (movementType) {
            case 0: // Yatay hareket
                int moveXDistance = (int)((30 + random.nextInt(70)) * speedMultiplier);
                ObjectAnimator moveX = ObjectAnimator.ofFloat(ghostImage, "translationX",
                        0, (random.nextBoolean() ? 1 : -1) * moveXDistance);
                moveX.setDuration((long)(duration * (2.0f - speedMultiplier * 0.5f))); // Yavaş level'larda daha uzun süre
                moveX.setRepeatCount(1);
                moveX.setRepeatMode(ValueAnimator.REVERSE);
                moveX.setInterpolator(new LinearInterpolator());
                moveX.start();
                break;

            case 1: // Dikey hareket
                int moveYDistance = (int)((30 + random.nextInt(70)) * speedMultiplier);
                ObjectAnimator moveY = ObjectAnimator.ofFloat(ghostImage, "translationY",
                        0, (random.nextBoolean() ? 1 : -1) * moveYDistance);
                moveY.setDuration((long)(duration * (2.0f - speedMultiplier * 0.5f)));
                moveY.setRepeatCount(1);
                moveY.setRepeatMode(ValueAnimator.REVERSE);
                moveY.setInterpolator(new AccelerateDecelerateInterpolator());
                moveY.start();
                break;

            case 2: // Zigzag hareket
                int zigzagIntensity = (int)(40 * speedMultiplier);
                ObjectAnimator zigzagX = ObjectAnimator.ofFloat(ghostImage, "translationX",
                        0, zigzagIntensity, -zigzagIntensity, zigzagIntensity, 0);
                ObjectAnimator zigzagY = ObjectAnimator.ofFloat(ghostImage, "translationY",
                        0, -zigzagIntensity/2, zigzagIntensity/2, -zigzagIntensity/2, 0);
                zigzagX.setDuration((long)(duration * (1.5f - speedMultiplier * 0.3f)));
                zigzagY.setDuration((long)(duration * (1.5f - speedMultiplier * 0.3f)));
                zigzagX.setInterpolator(new AccelerateInterpolator());
                zigzagY.setInterpolator(new DecelerateInterpolator());
                zigzagX.start();
                zigzagY.start();
                break;

            case 3: // Diagonal hareket (level 3+)
                if (level >= 3) {
                    int diagDistance = (int)((50 + random.nextInt(80)) * speedMultiplier);
                    ObjectAnimator diagX = ObjectAnimator.ofFloat(ghostImage, "translationX",
                            0, (random.nextBoolean() ? 1 : -1) * diagDistance);
                    ObjectAnimator diagY = ObjectAnimator.ofFloat(ghostImage, "translationY",
                            0, (random.nextBoolean() ? 1 : -1) * diagDistance);
                    diagX.setDuration((long)(duration * (1.8f - speedMultiplier * 0.4f)));
                    diagY.setDuration((long)(duration * (1.8f - speedMultiplier * 0.4f)));
                    diagX.setInterpolator(new BounceInterpolator());
                    diagY.setInterpolator(new BounceInterpolator());
                    diagX.start();
                    diagY.start();
                }
                break;

            case 4: // Dairesel hareket (level 6+)
                if (level >= 6) {
                    float radius = 60 * speedMultiplier;
                    ObjectAnimator circleX = ObjectAnimator.ofFloat(ghostImage, "translationX",
                            0, radius, 0, -radius, 0);
                    ObjectAnimator circleY = ObjectAnimator.ofFloat(ghostImage, "translationY",
                            0, 0, radius, 0, 0);
                    circleX.setDuration((long)(duration * (1.3f - speedMultiplier * 0.2f)));
                    circleY.setDuration((long)(duration * (1.3f - speedMultiplier * 0.2f)));
                    circleX.setInterpolator(new LinearInterpolator());
                    circleY.setInterpolator(new LinearInterpolator());
                    circleX.start();
                    circleY.start();
                }
                break;

            case 5: // Teleport hareket (level 10+)
                if (level >= 10) {
                    // İlk yarıda bir yere teleport, sonra tekrar hareket
                    new Handler().postDelayed(() -> {
                        if (ghostImage.getParent() != null && !isPaused) {
                            int newX = random.nextInt(Math.max(1, screenWidth - 200));
                            int newY = random.nextInt(Math.max(1, screenHeight - 200));

                            ObjectAnimator teleportX = ObjectAnimator.ofFloat(ghostImage, "translationX", newX - ghostImage.getLeft());
                            ObjectAnimator teleportY = ObjectAnimator.ofFloat(ghostImage, "translationY", newY - ghostImage.getTop());
                            teleportX.setDuration(100);
                            teleportY.setDuration(100);
                            teleportX.start();
                            teleportY.start();
                        }
                    }, duration / 3);
                }
                break;
        }
    }

    private void animateGhostGrowth(ImageView ghostImage, int startSize, int endSize, int duration) {
        ValueAnimator sizeAnimator = ValueAnimator.ofInt(startSize, endSize);
        sizeAnimator.setDuration(duration);
        sizeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        sizeAnimator.addUpdateListener(animation -> {
            if (!isPaused) {
                int currentSize = (int) animation.getAnimatedValue();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) ghostImage.getLayoutParams();
                if (params != null) {
                    params.width = currentSize;
                    params.height = currentSize;
                    ghostImage.setLayoutParams(params);
                }
            }
        });

        sizeAnimator.start();
    }

    private int calculatePoints(int basePoints, int currentSize, int startSize, int endSize) {
        float sizeRatio = (float)(currentSize - startSize) / (endSize - startSize);
        // Level bonus ekle
        float levelMultiplier = 1.0f + (level * 0.1f); // Her level %10 bonus
        float sizeMultiplier = 4.0f - (3.0f * sizeRatio); // Küçükken 4x, büyükken 1x
        return Math.round(basePoints * sizeMultiplier * levelMultiplier);
    }

    private void catchGhost(GhostWithSize ghost, int points) {
        ghostContainer.removeView(ghost.getImageView());

        score += points;
        ghostsCaught++;
        scoreText.setText("Score: " + score + " | Level: " + level);

        checkLevelUp();
    }

    // Genişletilmiş Ghost sınıfı
    private static class GhostWithSize {
        private ImageView imageView;
        private int basePoints;
        private int duration;
        private String type;
        private int startSize;
        private int endSize;
        private int movementType;

        public GhostWithSize(ImageView imageView, int basePoints, int duration, String type,
                             int startSize, int endSize, int movementType) {
            this.imageView = imageView;
            this.basePoints = basePoints;
            this.duration = duration;
            this.type = type;
            this.startSize = startSize;
            this.endSize = endSize;
            this.movementType = movementType;
        }

        public ImageView getImageView() { return imageView; }
        public int getBasePoints() { return basePoints; }
        public int getDuration() { return duration; }
        public String getType() { return type; }
        public int getStartSize() { return startSize; }
        public int getEndSize() { return endSize; }
        public int getMovementType() { return movementType; }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Resume ghost spawning if needed
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1);
            startGhostSpawning();
        }

        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Pause music when app is in background
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdown();
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}