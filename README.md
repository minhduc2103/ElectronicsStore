# Electronics Store (Android Kotlin Project)

## Description
là ứng dụng thương mại điện tử về các sản phẩm điện - điện tử.  
Ứng dụng cho phép người dùng **đăng nhập, tìm kiếm, xem chi tiết sản phẩm, thêm vào giỏ hàng, đặt hàng và theo dõi đơn hàng** theo thời gian thực.  
Toàn bộ dữ liệu được đồng bộ **Realtime với Firebase**.

## Tech Stack & Architecture
- **Project Architecture:** `MVC (Model - View - Controller)`
- **Realtime Database:** Firebase Realtime Database
- **Authentication:** Firebase Authentication (Sign In, Sign Up, Sign Out, Forgot Password, Change Password, Show User Profile)
- **Local Storage:** Room Database (giỏ hàng offline)
- **User Session:** SharedPreferences (lưu trạng thái đăng nhập)
- **Event Handling:** EventBus Library
- **UI Components:**
    - `TabLayout + ViewPager2 + Fragment`
    - `BottomNavigationView + ViewPager2 + Fragment`
    - `ViewPager2 + CircleIndicator3` (auto slide banner)
- **Image Loading:** Glide Library (load ảnh từ URL)
- **RecyclerView Handling:** Tối ưu hiển thị khi nằm trong NestedScrollView

