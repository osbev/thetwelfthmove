# thetwelfthmove Project Structure

This document outlines the current directory structure of the `thetwelfthmove` project.


```
thetwelfthmove/
├── .gitignore      #Git ignore file for the project.
├── project-structure.md      #This file, documenting the project structure.
├── schema.sql      #Database schema file.
├── backend/      #Contains the Java backend application using Maven.
│   ├── pom.xml       #Maven project configuration file.
│   ├── src/      
│   │   ├── main/       
│   │   │   └── java/       
│   │   │       └── com/
│   │   │           └── thetwelfthmove/     #Main source code.
│   │   │               ├── App.java      #Main application class.
│   │   │               ├── controllers/
│   │   │               │   ├── AuthController.java     #Authentication controller.
│   │   │               │   └── GameController.java     #Game controller for chess game logic.
│   │   │               ├── dao/
│   │   │               │   ├── DatabaseConnection.java     #Database connection utility.
│   │   │               │   ├── GameDAO.java      #Data access object for Game model.
│   │   │               │   └── PlayerDAO.java      #Data access object for Player model.
│   │   │               ├── models/
│   │   │               │   ├── ChessBoard.java     #Chess board model class.
│   │   │               │   ├── ChessPiece.java     Chess piece model class.
│   │   │               │   ├── Game.java     #Game model class.
│   │   │               │   ├── Move.java     #Move model class.
│   │   │               │   └── Player.java     #Player model class.
│   │   │               └── utils/
│   │   │                   └── JWTUtil.java      #Utility for JWT operations.
│   │   └── test/
│   └── target/
│       ├── classes/
│       │   └── com/
│       │       └── thetwelfthmove/
│       │           ├── App.class
│       │           ├── controllers/
│       │           │   ├── AuthController.class
│       │           │   └── GameController.class
│       │           ├── dao/
│       │           │   ├── DatabaseConnection.class
│       │           │   ├── GameDAO.class
│       │           │   └── PlayerDAO.class
│       │           ├── models/
│       │           │   ├── ChessBoard.class
│       │           │   ├── ChessBoard.class
│       │           │   ├── ChessPiece.class
│       │           │   ├── Game.class
│       │           │   ├── Move.class
│       │           │   └── Player.class
│       │           └── utils/
│       │               └── JWTUtil.class
│       ├── generated-sources/
│       │   └── annotations/
│       ├── generated-test-sources/
│       │   └── test-annotations/
│       ├── maven-status/
│       │   └── maven-compiler-plugin/
│       │       ├── compile/
│       │       │   └── default-compile/
│       │       │       ├── createdFiles.lst
│       │       │       └── inputFiles.lst
│       │       └── testCompile/
│       │           └── default-testCompile/
│       │           ├── createdFiles.lst
│       │           └── inputFiles.lst
│       └── test-classes/
└── frontend/     #Contains the React frontend application using Vite.
    ├── eslint.config.js      #ESLint configuration.
    ├── index.html      #Main HTML file.
    ├── package-lock.json
    ├── package.json
    ├── README.md
    ├── vite.config.js
    ├── public/
    │   └── favicon.png
    └── src/      #Source code.
        ├── App.jsx     #Main App component.
        ├── index.css
        ├── main.jsx      #Entry point.
        ├── assets/
        │   └── react.svg
        ├── components/     #React components.
        │   ├── AuthPage.jsx      # Authentication page component.
        │   ├── ChessBoard.jsx      #Chess board component.
        │   ├── ChessPieces.jsx
        │   ├── Dashboard.jsx
        │   ├── GamePage.jsx
        │   ├── Login.jsx
        │   ├── ProtectedRoute.jsx
        │   └── Signup.jsx
        ├── context/
        │   └── AuthContext.jsx     #Authentication context.
        └── styles/     #Stylesheets.
            ├── chessboard.css
            ├── dashboard.css
            ├── gamepage.css
            ├── login.css
            ├── main.css      #Main styles.
            ├── root.css      #Root styles.
            └── signup.css
```