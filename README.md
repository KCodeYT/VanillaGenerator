![banner](./.github/images/banner.png)

What is this?
------------------------------

This project is currently only a proof-of-concept work to present that a vanilla generator based on a background server
would theoretically and practically work.
This project adds a vanilla generator to the [powernukkitx](https://github.com/PowerNukkitX/PowerNukkitX/) server
software.

⚠️ This plugin only works with powernukkitx for minecraft bedrock edition 1.19.40 (protocol 557) ⚠️

How can I download this plugin?
------------------------------

You can find the downloads on the [releases section](https://github.com/KCodeYT/VanillaGenerator/releases) of this
github
page.
There you can find the LATEST jar file of this plugin.

How can I access the generator?
------------------------------

    0. ⚠️ DO NOT RUN THIS PLUGIN ON A PRODUCTION SERVER ⚠️
             (its not stable and uses much resources)
    1. Add this plugin to your server.
    2. Open the nukkit.yml file of your server.
       You need to add a new world to the worlds config in the nukkit.yml file.
       This world needs the generator "vanilla" for the overworld.
       If you want the world to be nether, then the generator needs to be "vanilla_nether".
       If you want the world to be the end, then the generator needs to be "vanilla_the_end".
    3. Save the file and start your server.

Example nukkit.yml world config (with all 3 world types):

```xml
worlds:
  world:
    seed: 0123456789
    generator: vanilla
  nether:
    seed: 0123456789
    generator: vanilla_nether
  the_end:
    seed: 0123456789
    generator: vanilla_the_end
```

How does this plugin work?
------------------------------

This plugin works by adding 3 new generators to the server (vanilla, vanilla_nether, vanilla_the_end).
Those generators start a bedrock dedicated server in the background. After they started bots join those servers and
generate chunks continuously and clone them to the powernukkitx server when they are needed.
As its well known that a bedrock dedicated server uses a lot of resources, with this plugin your server needs those
resources too.
