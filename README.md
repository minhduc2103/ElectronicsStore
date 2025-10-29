# Electronics Store (Android Kotlin Project)

## Description
This is an **e-commerce application** for **electrical and electronic products**.  
The app allows users to **log in, search, view product details, add items to the cart, place orders, and track their orders in real time**.  
All data is synchronized **in real time with Firebase**, ensuring a smooth and responsive user experience.

## Tech Stack & Architecture
- **Project Architecture:** `MVC (Model - View - Controller)`
- **Realtime Database:** Firebase Realtime Database
- **Authentication:** Firebase Authentication (Sign In, Sign Up, Sign Out, Forgot Password, Change Password, Show User Profile)
- **Local Storage:** Room Database (cart offline)
- **User Session:** SharedPreferences (Save login state)
- **Event Handling:** EventBus Library
- **UI Components:**
    - `TabLayout + ViewPager2 + Fragment`
    - `BottomNavigationView + ViewPager2 + Fragment`
    - `ViewPager2 + CircleIndicator3` (auto slide banner)
- **Image Loading:** Glide Library (load image from URL)

