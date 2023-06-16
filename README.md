# CalisApp

## Introduction
Calis app is designed to help children learn to read, write and count. It offers lessons and questions categorized by lesson type and level. Children can practice reading, writing, and counting through different types of tasks, including text-based questions, audio-based dictation, and handwritten exercises.

## Installation
Go to the **<> Code** button and copy the project [link](https://github.com/Calis-Top-10/CalisApp.git) . <br>
![image](https://github.com/Calis-Top-10/CalisApp/assets/89125601/4a05d16b-d699-40ae-baf2-d136c4607c67) <br>
Open terminal or command prompt on your local machine. <br>
Navigate to the directory where you want to clone the app. You can use the **cd** command to change directories. For example, to navigate to the "Documents" directory:
```
cd Documents
```
Use the **git clone command** followed by the URL you copied.
```
git clone https://github.com/Calis-Top-10/CalisApp.git
```

# Requirements
The app is written in Kotlin and uses the Gradle build system.
To build and run the Hidup Sehat Android application, ensure you have the following software installed:
<ol>
  Android Studio: 2022.2.1 (Flamingo)
  Minimum SDK: 24
  Target SDK: 33
  JDK: 17
  Kotlin: 1.8
  Android Gradle Plugin: 8.0.0
  Gradle: 8.0
</ol>

## Sign in
To experience learning in Calis, user can just simply login with their Google Account. Google Sign-In automatically detects and authenticates user activity in the application.

## Add Profile
Allow users to create individual profiles with customizable settings and progress tracking where one Google account can be linked to multiple child profiles. In creating a profile, the user needs to input the child's name and age. 

## Read Lesson
At the beginner stage, the lesson is the alphabet. At the intermediate level, lessons are in the form of words and sentences for the advanced stage. When the application will display the alphabet, and the user will read the lesson. The application provides instructions to the user via a question mark at the top right. To submit an answer, the user will press the microphone button and say the answer, then the application will check the answer.

## Write Lesson
The beginner stage is about writing the alphabet, the intermediate stage is about writing words, and the advanced stage is writing sentences. The application detects the user's handwriting through the handwriting results. To submit an answer, the user will write the answer in the canvas box, then the application will check the answer. Currently, we have implemented beginner stage, we plan to continue intermediate and advanced stage in the future.

## Counting Lesson
At the beginner stage, the application introduces children to numbers. There will be number sound and user will write number like writing lesson. At an intermediate stage, the application displays addition and subtraction using pictures. In the advanced stage, numbers will be used instead of pictures. To submit an answer, the user will write the answer in the canvas box, then the application will check the answer. 

## Report Progress Tracking 
Provide users with a visual representation of their progress, user activity in the past week, and failed lessons for improvement. Every user learning will be recorded and saved on our cloud. This report is dedicated to the children’s parents.

## Personalized Learning
Offer adaptive learning experiences that adjust difficulty levels based on users performance and learning pace. If the child answers a question wrong, similar questions will appear in the Pengayaan section. With this, the child will focus on improving their weaknesses

