# Smash Character Picker (v5.0)

A program designed to pick random matchups for the game Super Smash Bros. For those of us who don't like using the random character option, because it usually results in an unfair matchup. Instead, this program uses a tier list to create a matchup that is fair. Tier lists are defined in a `.txt` file. There are some additional settings to customize the experience, as well as some systems to ensure novel matchups are generated. Also includes a soundboard featuring 21 sound effects!

## Usage

When you launch the program, the first order of business will be to load a tier list. Simply use the "Load" button in the bottom left corner of the window. Once you find your tier list file, if it is valid, the program will open it without issue. To learn how to format your tier list correctly, see the next section. When the tier list is loaded, you are now ready to generate matchups! Simply hit the "Generate" button, and a matchup will be generated. If you don't like the matchup you got, just use the "Skip" button. Normally players will never be allowed to get the same fighter twice in one session. But if you skip the battle, players will eventually be able to get that fighter again. See the section on the "Cannot Get" buffer for more details.

### Switching Fighters

If you and another player wish to swap which fighter you got, you can do so using the switch panel on the right side of the window. Simply select the two players who wish to swap and hit the "Switch" button. Simple as that.

**Note**: Hitting the "Switch" button without any players selected can cause errors. It shouldn't actually affect the program in any way, but it is still not advisable to use the switch panel in ways that are not intended by the developer.

## Tier List Format

See the example `tier list.txt` file for an example of how to format a tier list. For posterity, the format is also described here.

Each tier goes on its own line. Simply start the line with the name of the tier (i.e. "double s" or "mid c"). Each tier is split into three. For example, there is an upper A, mid A, and lower A. The only tier this does not apply to is double S, there is only one SS tier. Unlike previous versions of the program, you do not need to include *all* tiers, you only need to include the ones you're using. They can also be put in any order now. As mentioned, a line which defines a tier must start with the name of that tier. It should be followed by an equals sign with a space on either side of it. After that, you can start naming characters. Simple list each character, separated by a comma and space. Note that character names like "Bowser, Jr." will be registered as two different characters, "Bowser" and "Jr.", so be sure to not include a comma in any character's name.

Each player also gets what is called an "exclusion list", which is the list of characters that they never want to be able to get. For those characters that you just can't stand.

Finally, various settings can be defined inside your tier list file, that way you don't have to change them every time you run the program. These are as follows:

- `tier chances = ` will allow you to define custom tier chances. Make sure you include *all* 8 numbers, separated by a comma and space in the same way you did when defining tiers. Also make sure that the numbers add up to 100, otherwise the program will reject them.
- `cannot get size = ` will let you specify the size of the "Cannot Get" queue. It can be any number from 0 to 15. See the next section for more details.
- `allow ss in cannot get = ` This setting can be either `true` or `false`. Determines whether or not SS tier characters are allowed in the "Cannot Get" queue.
- `allow s in cannot get = ` This setting can be either `true` or `false`. The same as the above setting, but for S tier characters.
- `players = ` will allow you to set the number of players. A number between 2 and 8 is required.

## Changing Options

There are a handful of options to customize the experience to exactly what you want. You can select between 2 and 8 players using the spinner next to the "Generate" button. All other settings are located on the left side of the window. You can change the percentage chance of each tier being picked using the "Tier chance settings" panel. Simply input your percentages and hit the "Apply" button. As long as your percentages add up to 100, your custom percentage chances will now be used. As mentioned above, all settings can be defined within the tier list file, so you don't have to change them every time you run the program.

The other settings deal with the "Cannot Get" buffer, which is detailed below.

### The "Cannot Get" Buffer

In order to ensure novel matchups, the program employs what I call the "Cannot Get" buffer. Essentially, this is a buffer which contains all the fighters that have been played in the last X number of battles, with X being the size of the "Cannot Get" buffer. The buffer ensures that *nobody* will be able to get a fighter that has already been played within the last X number of battles. There is also an individual list for each player consisting of all the fighters they've gotten during the session. A player will *never* be able to get the same fighter twice during the same session, unless the "Skip" button was used.

For example, let's say that we're using a buffer size of 5, and in the first battle, player 1 gets Mario and player 2 gets Luigi. *Neither* player will be able to get Mario or Luigi until battle number 6. However, player 1 will not be able to get Mario *for the rest of the session*, and player 2 will not be able to get Luigi again *for the rest of the session*.

There is also the option as to whether or not SS or S tier characters are allowed in the "Cannot Get" buffer. If your SS or S tiers are particularly small, you may not want to include them in the "Cannot Get" buffer. Or perhaps you find the best characters in the game to be the most fun, so you don't want to limit the number of times you can play them.

**Note**: You do not want to make the "Cannot Get" buffer size too big, otherwise the program may freeze. The program may encounter a situation in which there are no characters to choose from, because they're all in the "Cannot Get" buffer. In that case, the program will freeze. Be sure to set your buffer size carefully. Essentially, you want to ensure there are always enough valid characters to choose from for each player at all times.