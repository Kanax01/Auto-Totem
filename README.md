<div align="center">

# Auto Totem
### By Kanax01

A simple auto totem mod for Minecraft.

</div>


## Requirements
- Minecraft Java Edition 26.1.2
- [Fabric Loader](https://fabricmc.net/)
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Cloth Config](https://modrinth.com/mod/cloth-config)
- [Mod Menu](https://modrinth.com/mod/modmenu)

## Installation
Drop the `.jar` into your `.minecraft/mods` folder.

## Disclaimer
- **I AM NOT RESPONSIBLE IF YOU GET BANNED FROM A SERVER**
### Some unsafe servers (for now) are:
- **pvplegacy**
- **flowpvp**
- **pvpclub**

## Modes
- **Regular** — Only replaces the totem in your offhand
- **Legit** — Swaps to a hotbar totem when health is low and replenishes both hands

## Utils
- **Damage Predict** — Equips a totem in dangerous situations if totem not on and in legit mode it double hands
- **Gapple Bind** — Swaps your offhand with a gapple when holding a selected item and uses it
- **Gapple Bind Main** — Swaps the selected item in your main hand with a gapple for quick use

## Misc
- Adjustable delay
- Hide totem pop animation
- Hide totem particles

## Configuration
Settings can be configured in-game via [Mod Menu](https://modrinth.com/mod/mod-menu).

| Setting | Description | Default |
|---|---|---|
| Mode | Regular or Legit mode | Regular |
| Legit Health Threshold | Health where Legit mode refills hotbar totems | 10 |
| Damage Predict | Auto equip totem in dangerous situations | Off |
| Danger Threshold | Effective health threshold for danger checks | 10 |
| Gapple Bind | Enable gapple bind | Off |
| Gapple Bind Main | Enable main hand gapple bind | Off |
| Gapple Trigger | Item type required to activate gapple bind | Sword |
| Swap Delay | Delay between swaps in ms (safe minimum is enforced internally) | 15 |
| Hide Animation | Hide the totem pop animation | Off |
| Hide Particles | Hide totem pop particles | Off |
