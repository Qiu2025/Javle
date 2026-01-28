# Javle - Java Learning App 🚀

**Integrantes:**
- Jiade Zheng - [@jiade-git](https://github.com/jiade-git)
- Yixiao Yao - [@MASKYX](https://github.com/MASKYX)
- Siyuan Qiu - [@Qiu2025](https://github.com/Qiu2025)
- Shuhang Pan - [@usshng](https://github.com/usshng)
  
Javle is an Android application designed to learn and practice Java interactively. The project leverages Artificial Intelligence to generate personalized challenges and integrates with external platforms to provide real-world algorithm problems.

## ✨ Key Features 
Link to a presentation (in Spanish): https://www.canva.com/design/DAG86XJxqqo/0hkG2xX90VmcMxjMzKOjYA/view?utm_content=DAG86XJxqqo&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=heba34db684
- AI-Powered Challenges: Generation of multiple-choice questions (MCQs) about Java programming using the Google Gemini API. These tests are designed to evaluate and improve your Java programming fundamentals.

- Problem of the Day: A randomly selected problem from LeetCode for daily practice!

- Error History: A system that saves mistakes done by users so they can review the concepts they found the most hard.

- Local Persistence: Uses an SQLite database to manage the challenge queue, user progress, and saved errors.

- Notifications: A notification system that tells the user when the questions have been refilled in the database.

## 🛠️ Technologies Used
- Language: Java 17

- Development Environment: Android Studio.

- Artificial Intelligence: Google Gemini API (Dynamic content generation).

- Database: SQLite for local data storage.

- Networking: API consumption via HttpURLConnection and data processing with Gson.

## 🚀 **Important** Configuration
To enable the challenge generation, you must configure the Gemini API Key:

Open the local.properties file in the root of your project.

Add the following line:

Properties

GEMINI_API_KEY=YOUR_API_KEY_HERE

And also have the following versions:
* **Minimum SDK:** 24 (Android 7.0 Nougat)
* **Target SDK:** 36 (Android 16 - Preview/Experimental)
* **Java Version:** 17

And finally, build the project from Android Studio.
