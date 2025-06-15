# 📱 TRIS! – Android Tic-Tac-Toe Game

**TRIS!** is a mobile Android app inspired by the classic Tic-Tac-Toe game (3x3 grid), developed by **Giacomo Sagliano** as part of a university project. With a vertical layout and sleek interface, it adds multiplayer functionality and strategic features for a modern experience.

---

## 📱 Features

- **🔐 Authentication System**  
  Secure login and registration. All data is stored in Firebase Realtime Database.

- **👤 User Profile**  
  View registration date, last access, and match stats.

- **🏆 Leaderboard**  
  Compete in public games and climb the global rankings. Private and offline matches do not affect your score.

- **🌐 Public Matches**  
  Real-time matchmaking with a 10-second turn timer. Players have 40 seconds to reconnect before forfeiting.

- **🔒 Private Matches**  
  Create a game room with a custom password to challenge a friend. Same rules apply as public matches.

- **🤖 Offline Mode vs AI**  
  Train against three levels of AI:
  - **Easy**: random moves
  - **Medium**: detects win conditions
  - **Hard**: detects win conditions and blocks player strategies

- **💾 Game Resume (Room)**  
  Unfinished games are saved locally using Room and can be resumed later.

---

## 🧠 AI & Strategy

The app includes a smart algorithm: **AlphaBetaPro**, which can be tested in offline mode for a more competitive AI challenge.

The ViewModel acts as the game server, controlling:
- Turn management
- Win detection
- Disconnection handling

---

## ⚙️ Tech Stack

- **Language**: Java
- **UI**: XML + Android View system
- **Remote DB**: Firebase Realtime Database
- **Local DB**: Room (AndroidX)
- **Architecture**: MVVM with LiveData and ViewModel
- **Other Libraries**:
  - Material Design
  - ConstraintLayout
  - Firebase BOM
  - RoundedImageView
  - SDPs (for screen size adaptation)

---
