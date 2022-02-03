# Smash Character Picker (v2.0)

A program designed to pick random matchups for the game Super Smash Bros. For those of us who don't like using the random character option, because it usually results in an unfair matchup. Instead, this program uses a tier list to create a matchup that is fair. Tier lists are defined in a `.txt` file. There are some additional settings to customize the experience.

## Usage

The program runs in a small window. The first order of business will be to load a tier list. Under "File", select the "Load tier list" option. Once you find your tier list file, if it is valid, the program will open it without issue. You are now ready to generate matchups! Simply hit the "Generate" button, and a matchup will be generated. The matchup will be printed in the text box on the right side of the window. If you wish to change some options, that can be done on the left side.

## Changing Options

There are a few options to customize exactly what kind of experience you want. You can select from 2 to 8 players. If desired, you can force a particular tier to be chosen. Finally, you can choose between some options regarding lower tiers. These are as follows:

- No tiers below C: Will select any tier from C tier up to double S tier.
- Use "Playable Low Tiers": If you simply want to use a particular group of low tier characters, use this option. More or less the same as just having a D tier, though.
- Allow D tier and above: Will use tiers D through double S tier.
- Allow D & E tiers and bove: Will use tiers E through double S tier.
- Allow all low tiers: Will use all tiers.

Note: There is an "Additional Settings" button. It doesn't do anything. Sorry about that.

## Tier List Format

Under the "Help" section of the menu bar, you see how the tier list must be formatted. An example tier list is also included in this repository. For posterity, the format is repeated below:

>All lines should contain comma-separated entries.
>Leave area after equals sign blank if N/A.
>Do not omit lines, otherwise it will not load properly.
>Note that you cannot put the lines in any order, either. They must be in this order.

>doubS=
>tierS=
>tierA=
>tierB=
>tierC=
>tierD=
>tierE=
>tierF=
>lowTs=(list of playable low tiers, if you like to have just a fiew low tiers in the pool)
>p1Exc=(exclusion lists for each player, for those fighters you just can't stand)
>p2Exc=
>p3Exc=
>p4Exc=
>p5Exc=
>p6Exc=
>p7Exc=
>p8Exc=