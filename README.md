# Space Colony: Pioneers

A strategic space colony management game where you command a crew, manage resources, and survive alien threats. Build your team, complete missions, and reach 100 fragments to win!

## 🎮 Game Overview

You are the commander of a space colony. Your mission is to keep the colony growing, improve your crew's power through missions and training, and collect resources to achieve victory.

### Victory Condition
- **Collect 100 fragments** to win the game!

## ✨ Features

- **5 Unique Professions**: Medic, Engineer, Soldier, Scout, and Commander
- **Strategic Combat**: Turn-based battles with skills, shields, and energy management
- **Crew Management**: Assign crew to different areas for various benefits
- **Mission System**: Choose missions, build squads, and face challenging enemies
- **Progression System**: Level up crew, gain XP, and unlock powerful abilities
- **Save/Load System**: Save your progress and continue anytime
- **Dynamic Difficulty**: Enemy AI adapts with buffs, debuffs, and varied strategies

## 🏠 Core Gameplay Loop

### 1. Scheduling Phase
Assign your crew to three key areas:
- **Quarters**: Crew recovers full energy. Medic boosts recovery for all crew here
- **Training Simulator**: Crew gains XP and levels up faster
- **Mission Control**: Prepares crew for combat missions

### 2. Progression Phase
Process daily effects:
- Crew in quarters recover HP and energy
- Crew in simulator gain experience points
- Mission control crew prepare for upcoming missions

### 3. Mission Selection Phase
- Choose from available missions
- Build your squad (up to 5 members)
- Check difficulty warnings and squad bonuses
- Start the mission when ready

### 4. Combat Phase
Turn-based tactical combat:
- Select crew members to attack or use skills
- Monitor enemy intents before they act
- Manage shields, energy, and health
- Defeat all enemies to complete the mission

## 👥 Crew Professions

### Medic
- **Role**: Healing specialist
- **Special**: Boosts recovery for all crew in quarters
- **Skill**: Heal - Restore HP to teammates
- **Stats**: High energy, moderate HP

### Engineer
- **Role**: Defense and protection
- **Skill**: Repair - Generate energy shields
- **Stats**: Strong defense, good HP

### Soldier
- **Role**: Frontline damage dealer
- **Skill**: Rage Shot - Powerful single-target attack
- **Stats**: Highest attack power

### Scout
- **Role**: Reconnaissance and weakening enemies
- **Skill**: Scout - Debuff enemies
- **Stats**: Agile, high energy

### Commander
- **Role**: Team leader and buffer
- **Skill**: Inspire - Boost squad morale and combat power
- **Stats**: Balanced stats, team support

## 🎯 Installation Guide

### Prerequisites
- **Android Studio**: Arctic Fox (2020.3.1) or later
- **JDK**: Java Development Kit 11 or higher
- **Android SDK**: API level 21 (Android 5.0) or higher
- **Gradle**: Version 8.7 or compatible

### Step-by-Step Installation

#### 1. Clone the Repository
```bash
git clone <repository-url>
cd MyApplication5
```

#### 2. Open in Android Studio
- Launch Android Studio
- Select "Open an Existing Project"
- Navigate to the project directory and select it
- Wait for Gradle sync to complete

#### 3. Configure Build Settings
The project uses the following configuration:
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: Latest stable version
- **Build Tools**: Configured in `build.gradle`

#### 4. Sync Gradle
- Android Studio should automatically sync Gradle
- If not, go to **File > Sync Project with Gradle Files**
- Ensure all dependencies download successfully

#### 5. Build the Project
- Go to **Build > Make Project** (or press `Ctrl+F9`)
- Check for any build errors in the Build window
- Fix any issues if they appear

#### 6. Run on Device/Emulator

**Option A: Physical Device**
1. Enable Developer Options on your Android device
2. Enable USB Debugging
3. Connect device via USB
4. Click **Run** button in Android Studio
5. Select your device from the list

**Option B: Emulator**
1. Open **AVD Manager** (Tools > AVD Manager)
2. Create a new virtual device (API 21+)
3. Start the emulator
4. Click **Run** button in Android Studio
5. Select the emulator

## 🔧 Troubleshooting

### Common Issues

**Gradle Sync Failed**
- Check your internet connection
- Go to **File > Invalidate Caches / Restart**
- Update Android Studio to the latest version

**Build Errors**
- Clean the project: **Build > Clean Project**
- Rebuild: **Build > Rebuild Project**
- Check that JDK path is correctly configured

**App Crashes on Launch**
- Check Logcat for error messages
- Ensure minimum SDK requirements are met
- Verify all dependencies are properly installed

**Performance Issues**
- Close other applications
- Increase emulator RAM allocation
- Use a physical device for better performance

## 📱 Controls

### Main Interface
- **Tap crew members** to select and view details
- **Assign buttons** to send crew to different areas
- **Next Phase** to advance to the next game phase
- **Save/Load** to manage game progress
- **Stats** to view detailed statistics
- **Guide** to access this tutorial

### Combat Interface
- **Select crew** by tapping their icon
- **Attack** - Basic attack against selected enemy
- **Skill** - Use special ability (consumes energy)
- **End Turn** - Finish your turn and let enemies act
- **Exit Combat** - Leave battle (win or retreat)

## 💡 Tips & Strategies

### Early Game
- Start with balanced squad composition
- Focus on completing easier missions first
- Keep at least one Medic for healing

### Mid Game
- Place Medic in Quarters to boost team recovery
- Use Training Simulator to level up key crew
- Build diverse squads for different mission types

### Late Game
- Maximize fragment collection efficiency
- Upgrade crew to handle high-difficulty missions
- Maintain resource balance for recruitment

### Combat Tips
- Always check enemy intent before acting
- Prioritize healing injured crew members
- Use skills strategically to conserve energy
- Focus fire on one enemy at a time
- Watch shield values and break them first

## 📊 Game Mechanics

### Energy System
- Each action consumes energy
- Energy fully restores in Quarters
- Manage energy carefully during missions

### Experience & Leveling
- XP is retained after missions
- Higher levels increase stats and skill effectiveness
- Training Simulator accelerates XP gain

### Injury System
- Injured crew cannot participate in combat
- Rest in Quarters to recover from injuries
- Plan missions around crew availability

### Squad Bonuses
- Different profession combinations provide bonuses
- Check bonus descriptions before starting missions
- Optimize squad composition for each mission type

## 🛠️ Technical Details

### Architecture
- **MVC Pattern**: Clear separation of Model, View, Controller
- **Manager Classes**: CombatManager, CrewManager, MissionManager, etc.
- **Custom Views**: CombatView for rendering battles
- **RecyclerView Adapters**: Efficient list rendering

### Key Components
- **GameState**: Central game state management
- **CombatState**: Battle state and logic
- **ProfessionConfig**: Profession definitions and balancing
- **StorageManager**: Save/load functionality using Gson

### Dependencies
- AndroidX libraries
- RecyclerView for lists
- Gson for JSON serialization
- Custom drawing with Canvas

## 📝 License

This project is for educational and demonstration purposes.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues and pull requests.

## 📞 Support

If you encounter any bugs or have suggestions, please open an issue in the repository.

---

**Enjoy commanding your space colony!** 🚀
