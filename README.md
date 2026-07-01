# Remember App 📝

一款基于 Jetpack Compose 的 Android 提醒应用，帮你记住重要的日子和事项。

## ✨ 功能特性

- **多种提醒类型**：生日、纪念日、出差、证件到期、账单缴费、自定义提醒
- **提前提醒**：支持设置提前多少天通知
- **每年重复**：生日、纪念日等支持每年自动重复
- **本地存储**：使用 Room 数据库，数据安全可靠
- **Material 3 设计**：现代简洁的用户界面

## 🛠 技术栈

| 技术 | 说明 |
|------|------|
| **Kotlin** | 开发语言 |
| **Jetpack Compose** | UI 框架（Material 3） |
| **Room** | 本地数据库 |
| **Navigation Compose** | 页面导航 |
| **ViewModel + StateFlow** | MVVM 架构 |
| **DataStore** | 偏好设置存储 |
| **KSP** | 注解处理 |

## 📱 项目结构

```
app/src/main/java/com/remember/app/
├── MainActivity.kt          # 主 Activity
├── RememberApp.kt           # Application 类
├── data/
│   ├── database/
│   │   ├── AppDatabase.kt   # Room 数据库
│   │   ├── dao/             # 数据访问对象
│   │   └── entity/          # 数据实体
│   └── repository/          # 数据仓库
├── model/                   # 数据模型
├── navigation/              # 导航配置
└── ui/
    ├── add/                 # 添加提醒页面
    ├── mine/                # 个人中心页面
    ├── reminder/            # 提醒列表页面
    └── theme/               # Material 3 主题
```

## 🚀 构建与运行

### 环境要求

- Android Studio Flamingo (2022.2.1) 或更高版本
- JDK 11+
- Android SDK 33
- Gradle 7.4.2

### 构建步骤

```bash
# 克隆项目
git clone https://github.com/cwh1234/remember_app.git
cd remember_app

# 构建 Debug APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

或在 Android Studio 中直接打开项目并运行。

## 📋 提醒类型

| 类型 | 说明 |
|------|------|
| 🎂 生日 | 支持每年重复 |
| 💑 纪念日 | 支持每年重复 |
| ✈️ 出差 | 单次提醒 |
| 📄 证件到期 | 单次提醒 |
| 💰 账单缴费 | 单次提醒 |
| 📌 自定义 | 灵活设置 |

## 📄 开源协议

MIT License
