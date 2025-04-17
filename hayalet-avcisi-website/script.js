// script.js
console.log("Hayalet Avcısı web sitesi çalışıyor!");

// Sayfa yüklendiğinde çalışacak fonksiyonlar
document.addEventListener('DOMContentLoaded', function() {
  // Navbar scroll efekti
  const navbar = document.querySelector('.main-nav');
  if (navbar) {
    window.addEventListener('scroll', function() {
      if (window.scrollY > 50) {
        navbar.style.background = 'rgba(10, 10, 10, 0.95)';
        navbar.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.5)';
      } else {
        navbar.style.background = 'rgba(10, 10, 10, 0.8)';
        navbar.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.3)';
      }
    });
  }

  // Smooth scroll için
  document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function(e) {
      e.preventDefault();
      const targetId = this.getAttribute('href');
      const targetElement = document.querySelector(targetId);
      
      if (targetElement) {
        window.scrollTo({
          top: targetElement.offsetTop - 80,
          behavior: 'smooth'
        });
      }
    });
  });

  // Parallax efekti
  window.addEventListener('scroll', function() {
    const parallaxElements = document.querySelectorAll('.hero-section, .features, .gameplay, .about, .contact');
    parallaxElements.forEach(element => {
      const speed = 0.05;
      const rect = element.getBoundingClientRect();
      const scrolled = window.pageYOffset;
      
      if (rect.top < window.innerHeight && rect.bottom > 0) {
        element.style.backgroundPositionY = `${scrolled * speed}px`;
      }
    });
  });

  // Animasyonlu sayaç
  const counters = document.querySelectorAll('.counter');
  const speed = 200;

  const animateCounter = (counter) => {
    const target = +counter.getAttribute('data-target');
    const count = +counter.innerText;
    const increment = target / speed;

    if (count < target) {
      counter.innerText = Math.ceil(count + increment);
      setTimeout(() => animateCounter(counter), 1);
    } else {
      counter.innerText = target;
    }
  };

  // Intersection Observer ile görünürlük kontrolü
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('animate');
        
        // Sayaç animasyonu için
        if (entry.target.classList.contains('counter')) {
          animateCounter(entry.target);
        }
      }
    });
  }, { threshold: 0.1 });

  // Animasyonlu elementleri gözlemle
  document.querySelectorAll('.feature-box, .step, .counter').forEach(el => {
    observer.observe(el);
  });

  // Fare takibi efekti
  const heroSection = document.querySelector('.hero-section');
  if (heroSection) {
    heroSection.addEventListener('mousemove', function(e) {
      const { clientX, clientY } = e;
      const { left, top, width, height } = heroSection.getBoundingClientRect();
      
      const x = (clientX - left) / width;
      const y = (clientY - top) / height;
      
      const moveX = (x - 0.5) * 20;
      const moveY = (y - 0.5) * 20;
      
      const heroImage = heroSection.querySelector('.hero-image');
      if (heroImage) {
        heroImage.style.transform = `translate(${moveX}px, ${moveY}px)`;
      }
    });
  }

  // Hayalet parçacık efekti
  createGhostParticles();

  // Mobil menü toggle
  const mobileMenuBtn = document.createElement('button');
  mobileMenuBtn.classList.add('mobile-menu-btn');
  mobileMenuBtn.innerHTML = '<i class="fas fa-bars"></i>';
  
  const navContainer = document.querySelector('.nav-container');
  if (navContainer) {
    navContainer.prepend(mobileMenuBtn);
    
    mobileMenuBtn.addEventListener('click', function() {
      const navLinks = document.querySelector('.nav-links');
      if (navLinks) {
        navLinks.classList.toggle('active');
        this.classList.toggle('active');
      }
    });
  }
});

// Hayalet parçacık efekti
function createGhostParticles() {
  const heroSection = document.querySelector('.hero-section');
  if (!heroSection) return;
  
  for (let i = 0; i < 15; i++) {
    const ghost = document.createElement('div');
    ghost.classList.add('ghost-particle');
    
    // Rastgele pozisyon
    const posX = Math.random() * 100;
    const posY = Math.random() * 100;
    
    // Rastgele boyut
    const size = Math.random() * 20 + 10;
    
    // Rastgele animasyon süresi
    const duration = Math.random() * 10 + 10;
    
    // Rastgele gecikme
    const delay = Math.random() * 5;
    
    ghost.style.left = `${posX}%`;
    ghost.style.top = `${posY}%`;
    ghost.style.width = `${size}px`;
    ghost.style.height = `${size}px`;
    ghost.style.animationDuration = `${duration}s`;
    ghost.style.animationDelay = `${delay}s`;
    
    heroSection.appendChild(ghost);
  }
}

// Sayfa yüklendiğinde preloader göster
window.addEventListener('load', function() {
  const preloader = document.querySelector('.preloader');
  if (preloader) {
    preloader.classList.add('fade-out');
    setTimeout(() => {
      preloader.style.display = 'none';
    }, 500);
  }
});

// CSS için ek stiller
const style = document.createElement('style');
style.textContent = `
  .preloader {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: var(--darker-color);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    transition: opacity 0.5s ease;
  }
  
  .preloader.fade-out {
    opacity: 0;
  }
  
  .preloader::after {
    content: '';
    width: 50px;
    height: 50px;
    border: 5px solid rgba(255, 255, 255, 0.1);
    border-top-color: var(--accent-color);
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
  
  @keyframes spin {
    to { transform: rotate(360deg); }
  }
  
  .ghost-particle {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    pointer-events: none;
    animation: float-ghost linear infinite;
    z-index: 1;
  }
  
  @keyframes float-ghost {
    0% {
      transform: translateY(0) translateX(0);
      opacity: 0;
    }
    10% {
      opacity: 0.8;
    }
    90% {
      opacity: 0.8;
    }
    100% {
      transform: translateY(-100px) translateX(50px);
      opacity: 0;
    }
  }
  
  .mobile-menu-btn {
    display: none;
    background: none;
    border: none;
    color: white;
    font-size: 1.5rem;
    cursor: pointer;
  }
  
  @media (max-width: 768px) {
    .mobile-menu-btn {
      display: block;
    }
    
    .nav-links {
      position: absolute;
      top: 100%;
      left: 0;
      width: 100%;
      background: rgba(10, 10, 10, 0.95);
      padding: 1rem;
      flex-direction: column;
      align-items: center;
      transform: translateY(-100%);
      opacity: 0;
      transition: all 0.3s ease;
      pointer-events: none;
    }
    
    .nav-links.active {
      transform: translateY(0);
      opacity: 1;
      pointer-events: auto;
    }
  }
  
  .feature-box, .step {
    opacity: 0;
    transform: translateY(30px);
    transition: opacity 0.5s ease, transform 0.5s ease;
  }
  
  .feature-box.animate, .step.animate {
    opacity: 1;
    transform: translateY(0);
  }
  
  .counter {
    font-size: 2.5rem;
    font-weight: bold;
    color: var(--accent-color);
  }
`;

document.head.appendChild(style);

// Preloader ekle
const preloader = document.createElement('div');
preloader.classList.add('preloader');
document.body.prepend(preloader);

// Sayaç ekle
document.addEventListener('DOMContentLoaded', function() {
  // Özellikler bölümüne sayaç ekle
  const featureContainer = document.querySelector('.feature-container');
  if (featureContainer) {
    const counterBox = document.createElement('div');
    counterBox.classList.add('feature-box');
    counterBox.innerHTML = `
      <i class="fas fa-trophy feature-icon"></i>
      <h3>Yakalanan Hayaletler</h3>
      <div class="counter" data-target="1000">0</div>
      <p>Oyuncularımız toplamda bu kadar hayalet yakaladı!</p>
    `;
    featureContainer.appendChild(counterBox);
  }
});