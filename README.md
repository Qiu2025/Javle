# Javle - Java Learning App
Javle is an Android application designed to learn and practice Java interactively. The project leverages Artificial Intelligence to generate personalized challenges and integrates with external platforms to provide real-world algorithm problems.

Link to a presentation (in Spanish): [Canva](https://www.canva.com/design/DAG86XJxqqo/0hkG2xX90VmcMxjMzKOjYA/view?utm_content=DAG86XJxqqo&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=heba34db684)

## Preview

<p align="center">
   <img src="/Screenshots/Home.png" width="20%">
   <img src="/Screenshots/Challenges.png" width="20%">
   <img src="/Screenshots/Problems.png" width="20%">
   <img src="/Screenshots/User.png" width="20%">
</p>

<details>
   <summary><b> Click for more </b></summary>
   <br>
   <p align="center">
     <img src="/Screenshots/AChallenge.png" width="20%">
     <img src="/Screenshots/History.png" width="20%">
     <img src="/Screenshots/AProblem1.png" width="20%">
     <img src="/Screenshots/AProblem2.png" width="20%">
     <img src="/Screenshots/Login1.png" width="20%">
     <img src="/Screenshots/Login2.png" width="20%">
     <img src="/Screenshots/Login3.png" width="20%">
</p>
</details>

## Features 
- **AI-Powered Challenges**: Generation of multiple-choice questions (MCQs) about Java programming using the Google Gemini API. These tests are designed to evaluate and improve your Java programming fundamentals.
- **Problem of the Day**: A randomly selected problem from LeetCode for daily practice!
- **Error History**: A system that saves mistakes done by users so they can review the concepts they found the most hard.
- **Local Persistence**: Uses an SQLite database to manage the challenge queue, user progress, and saved errors.
- **Notifications**: A notification system that tells the user when the questions have been refilled in the database.

## Technologies Used
- **Language**: Java 17
- **Development Environment**: Android Studio.
- **Artificial Intelligence**: Google Gemini API (Dynamic content generation).
- **Database**: SQLite for local data storage.
- **Networking**: API consumption via HttpURLConnection and data processing with Gson.

## Installation
### Prerequisites
- Android Studio  
- Java Development Kit

### Steps
1. Clone the repository  
   `git clone https://github.com/Qiu2025/Javle.git`
2. Open Android Studio and select "Open an existing project" pointing to the cloned directory.  
3. Let Android Studio download dependencies and sync the project.  
4. Configure your `GEMINI_API_KEY` in `local.properties` as follows: `GEMINI_API_KEY=YOUR_API_KEY_HERE`
5. Build and run the app on an Android device or emulator.

## Limitations
This project was developed in a limited time frame as part of an academic assignment. As a result, it has several limitations and areas that could be improved.
For example, AI-generated challenges may tend to be very similar to each other. This happens because each API call is independent and uses the same prompt structure, which can lead to repetitive outputs.  

The project prioritizes demonstrating core concepts (API integration, local persistence, Android components) rather than production-level robustness or optimization.
