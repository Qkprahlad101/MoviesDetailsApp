# MoviesListApp 🎬

MoviesListApp is a modern, AI-powered Android application built with Jetpack Compose that allows users to explore, search, and discover movies using the OMDb API and Google Gemini. It features a high-end UI with cinematic animations, AI-driven suggestions, and automated trailer discovery.

## 🔄 User Flow

The application follows a seamless discovery-to-viewing flow:

1.  **Cinematic Entry**: Experience a beautiful, AI-themed splash screen with orbital animations that sets the tone for an intelligent movie discovery experience.
2.  **Discovery (Home)**: Launch into a curated home screen featuring genre-based carousels (Action, Comedy, Sci-Fi) and **AI Suggestions** personalized based on your history.
3.  **Parallel Loading**: Genres load independently and incrementally, ensuring the UI is never blocked and content appears as soon as it's ready.
4.  **Real-time Search**: Use the global search bar to find any movie from the OMDb database with instant results and pagination.
5.  **Detailed Analysis**: Access a deep-dive view for any movie, including ratings from multiple sources, plot summaries, and cast info.
6.  **AI-Powered Playback**: The app uses an AI SDK to find the most relevant trailer on YouTube, allowing for instant in-app playback.
7.  **Safe Exit**: A themed confirmation dialog prevents accidental app closures, asking if you "really have to go."

## 📱 App Preview

Below is the visual representation of the app flow:

| 1. AI Splash Screen | 2. Home Screen (Discovery) | 3. Search Results |
| :---: | :---: | :---: |
| ![Splash Screen](screenshots/splash_screen.png) | ![Home Screen](screenshots/home_screen.png) | ![Search Result](screenshots/search_result.png) |

| 4. Sorting & Filtering | 5. Movie Details | 6. Trailer Playback |
| :---: | :---: | :---: |
| ![Filtered Search](screenshots/search_filtered_by_year_DESC.png) | ![Movie Details](screenshots/movie_details_screen.png) | ![Trailer Playing](screenshots/trailer_playing.png) |

## 🚀 Key AI Features

*   **AI Recommendations**: Powered by Gemini Pro, the app analyzes your top-rated movies to suggest new titles you'll love.
*   **Intelligent Validation**: Uses a resilient validation layer that handles network timeouts gracefully, ensuring you always get suggestions even if some provider data is slow.
*   **Automated Trailer Discovery**: Leverages the integrated TrailerAI SDK to scan YouTube for the most accurate movie trailers without manual searching.

## 🛠 Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **AI Engine**: Google Gemini Pro
- **Dependency Injection**: Koin
- **Networking**: Retrofit & OkHttp
- **Database**: Room (with LRU cleanup)
- **Image Loading**: Coil
- **Animation**: Compose Animations (Orbital particles, pulsing glows, and scan effects)
- **Architecture**: MVVM with Clean Architecture principles

## 📈 Recent Updates (v2.2.0)

- **AI Optimization**: Refactored the AI suggestion engine to work in parallel, resolving a 20s timeout issue and allowing for partial data returns.
- **Cinematic UI**: Added a new orbital-animation splash screen and a custom-designed AI-themed app icon.
- **Performance**: Implemented incremental carousel loading, reducing initial wait times by over 60%.
- **UX Improvements**: Added a themed "Exit Confirmation" dialog to prevent accidental closures.
- **Stability**: Integrated a fail-fast validation strategy for movie metadata to handle OMDb API latency gracefully.

---

## 🛠 Setup & Requirements

- Android Studio Ladybug or newer
- Min SDK: 24
- Target SDK: 36
- **Local Properties**: Add the following keys to your `local.properties`:
  - `OMDB_API_KEY`: Your OMDb API key.
  - `GEMINI_API_KEY`: Your Google AI Studio key.
  - `YOUTUBE_DATA_V3`: Your YouTube Data API key.
