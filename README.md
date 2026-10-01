# Smart Pantry Manager

A Java Android application that helps users reduce food waste by tracking
the ingredients they already have at home and suggesting recipes they can 
cook using strictly those ingredients, with no shopping trip required.

## Core Feature: Strict Matching

A recipe is only suggested if every ingredient it requires is in the
user's pantry in at leat the required quantity. Recipes missing even one
ingredient are excluded. Matching handles simple real-world differences
such as singular/plural names (tomato/tomatoes) and using difference
(g/kg, ml/l).

## Features
- Add, edit and delete pantry items (name, quantity, unit, optional expiry date)
- Pantry list screen (RecyclerView with custom adapter)
- 15-20 pre-loaded recipes seeded on first run
- Suggested Recipes screen showing only fully makeable recipes
- Recipe detail screen with ingredients and method
- Settings screen
- Input validation and an empty-state message when no recipes match

## Database: SQLite
SQLite (via SQLiteOpenHelper) was chosen because it runs locally on the
device, needs no internet connection or external account, and persists
data between sessions. A pantry app should work offline in the kitchen,
and the data is small and relational (pantry items, recipes and recipe
ingredients).

## Tech Stack
- Language: Java
- IDE: Android Studio
- Database: SQLite
- UI: RecyclerView, ConstraintLayout, Bottom Navigation


## Author
Oratile Modise, Richfield Graduate Institute of Technology
Mobile App Development 700