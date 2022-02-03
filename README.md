# Smash-Character-Picker

A program designed to pick random matchups for the game Super Smash Bros. For those of us who don't like using the random character option, because it usually results in an unfair matchup. Instead, this program uses a tier list to create a matchup that is fair.

## Usage

This version of the program is designed to be run inside the IDE or from the command line. All data and options are hardcoded. Simply run the program, and it will print out a matchup to the standard output.

## Changing Options

In order to make changes to the tier list or the chances of getting a particular tier, you'll need to edit the source code directly. The tier lists are defined in a series of ArrayList objects. A series of if statements are responsible for determining the chances of tiers. Otherwise, this version of the program is very bare-bones; there isn't much else to change.

The program currently supports three players. Changing the number of characters generated cannot be done easily. Each player does get an exclusion list, essentially an ArrayList containing characters that they *never* want to get.