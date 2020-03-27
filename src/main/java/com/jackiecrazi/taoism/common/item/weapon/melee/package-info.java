package com.jackiecrazi.taoism.common.item.weapon.melee;

/*
Weapons include:

    ***Melee weapons***

    single edge swords:
        high combo and power, medium range and defense, low speed.
        has a wide sweep radius, and generally have parry specials related to hitting harder or faster.
        Goes well in pairs or with a shield, dagger, cloak or other small sidearm, or even its own sheath.
    double edge swords:
        very high combo, medium range, defense, speed, power.
        has a small sweep, and can stab for increased damage.
        Parry specials center around successive hits.
        Versatile. Can combo with dagger, sword, shield, its own sheath, almost anything else, or as a sidearm
    axes:
        high power and defense, medium range, speed, combo.
        generally inflict higher posture damage and can cleave through armor.
        Parry specials defend well, or allow even more armor penetration
        Meant to be multipurpose, having decent combo potential with cleave while still being usable solo
    maces:
        very high power, medium defense and range, low speed and combo.
        generally centered around emptying the foe's posture bar.
        Parry specials center around CC to keep foes in range of power.
        Meant to be used in builds that maximize power, with shields or full-on berserk with a sidearm
    hand weapons:
        high speed and combo, medium power, mid-low defense, low range.
        can generally be switched in and out of without cooldown.
        A very diverse and unique class capitalizing on surprise and combos.
        Either used in pairs, with consumables, or with one hand open, for spells and grapples.
    daggers:
        high speed and combo, medium power, low range and defense.
        can be switched in and out without cooldown.
        Inflicts extra damage or extra effects on backstabs, encouraging trickery.
        Designed as sidearms or used in pairs, e.g. misericorde accompanying hammers or dual karambits.
    sticks:
        high defense and combo, medium power and speed, low range.
        Parries generally would charge up the other hand as well.
        Inflict controlling effects such as slow, stun, and mining fatigue.
        Excellent as sidearms to longer weapons, can function as mainhand weapons or dual-wielded.
    picks:
        high power and speed, medium combo and defense, low range.
        Controls opponents in your range of power.
        Inflicts stacking marks on the enemy that are detonated with parry specials or other weapons.
        Complement well with short range strikers on the offhand, such as daggers or itself.
    spears:
        high range and combo, medium power and speed, low defense
        Attacks anything in a line, dealing pierce damage.
        Two-handed, cannot be combined with another weapon.
        Parry specials boost both main and alt attack, increasing power or ameliorating combo.
    staves:
        high range and combo, medium speed and defense, low power
        Can swipe, smash, or stab. Encourages chaining of different attacks.
        Two-handed, cannot be combined with another weapon.
        Parry specials either add even more attacks or alleviate the low power issue slightly.
    polearms with axe-like blades:
        pole swords:
            high range and combo (retained from swords), medium speed and power, low defense
            Has two forms: a short-range form from grasping both ends and a long range form from holding one end.
            Switching from one form to another counts as an attack and has a slight cooldown.
            Only the short form can block with fast combos, the long range form trades combo ability for range, power, and sweep.
        pollaxes:
            high range and power, medium defense and speed, low combo
            Normal attack cleaves and reduces posture as a normal axe.
            As it consumes two slots, right click does a sweep slash.
            Has high burst and intimidation factor.
    warhammers:
        high range and power, medium defense, low speed and combo
        Reduces a huge amount of posture.
        Good at defense, parry specials center around control.
        The pure power of these weapons allow them to naturally ignore some armor.
    whips:
        high power and speed, medium range, low combo and defense
        Inflicts grievous wounds against unarmored targets, and very little against anything else.
        Utterly incapable of blocking, but whips around shield blocks
        As a flexible weapon, grabs onto various things, from projectiles (to whip them out) to blocks (as swings) to weapons (to disarm)
        Meant as a sidearm to armor-reducing weapons or as a mobility enabler
    weighted chains/ropes:
        high power and speed, medium defense, low combo and range
        Could be one-handed or two-handed depending on length of flexible matter, generally two-handed.
        Inertia-based attacks that charge up into a sudden binding strike.
        As a flexible weapon, grabs onto various things, from projectiles (to whip them out) to blocks (as swings) to weapons (to disarm)
        ^not as well as whips, unfortunately.
        Flexible as they themselves are.
    sectional weapons:
        high power and defense, medium speed and combo, low range
        Generally flexible in one-handed or two-handed usage, though using with only one hand will lock out some moves.
        Due to higher mass and less flexibility, can be used to parry, but cannot be used to grab onto things.
        Still hits around shields though.
        Unlike other flexible weapons, does not need to be charged up, and handles more like a slightly unpredictable staff.

    ***Ranged weapons***
        ***Cold ammo-consuming***
        All ammo consuming items can equip different forms of ammo for additional effects.
            bows:
                very high range, high combo, medium power, defense, speed
                Holdable charge-type sniper weapon.
                Shoots very far, suitable for a small opening salvo before engaging.
                Uses arrows.
            crossbows:
                very high defense, high power, medium range and speed, low combo
                Instant reload-type assault weapon.
                Shots have heavy knockback and pierce, and might even pin enemies down, but have significant reload time.
                Running reload is possible to capitalize on a run-n-gun style of play, like a heavy sling.
                Uses arrows and darts. They should use bolts but it's not significant enough to matter here.
            slings:
                very high combo, high range, medium speed, defense, power
                Continuous charge-type harassing weapon.
                The only continuous ranged weapon, shoots constantly when held down with slight windup.
                Uses clay shots, arrows, darts, and even blocks or stones from the ground if desperate.
                A prudent sling user would switch between ammo as they see fit, even mid-firing, racking up long combos.
            blowguns:
                very high speed, medium defense, range, combo, low power
                Instant reload-type stealth weapon.
                Weak by itself, it is used to inflict crippling debuffs on opponents before engaging in combat.
                Perfect for stealth ambushes in rainforests!
                Uses blowgun darts.
        ***Firearms***
            arquebuses:
                very high power and defense, medium range, low speed and combo
                Instant reload-type burst weapon.
                Shots deal a lot, like a lot, of damage.
                Running reload is not possible, so you have to find a good spot to reload.
                Certain models may allow multiple shots before reload.
                Generally requires two hands to use properly.
            handgonnes:
                honorable mention, but they go under the gadgetry module. Shoots small pellets or darts.
            fire lances:
                honorable mention, but they go under the gadgetry module. Sprays noxious fumes.
        ***Throwing weapons***
            darts:
                high speed and combo, medium power, low defense and range
                Primarily used in conjunction with crossbows slings, and miscellaneous gunpowder weaponry.
                Used by themselves they are without the windup normally associated with these weapons, but fly slightly slower.
                A jack-of-all-trades throwing weapon, I'd say.
            throwing knives/stars:
                high speed and combo, medium-low power and range, low defense
                Capitalizing on deception, throwing them is soundless and can be done rapidly.
                They don't have much in the damage department, however.
            throwing axes:
                high power, medium defense, range, combo, low speed
                Chargeable weapon. Has an obvious windup time.
                Don't fly very far, but act as a ranged version of an axe, cleaving as usual.
                Unlike other throwing weapons, also usable as a pretty good melee weapon.
            throwing sticks:
                high defense and range, medium power and speed, low combo
                Chargeable weapon. Has a short, but noticeable windup time.
                Hits generally slow opponents, may stun if they're small enough.
                Can also be used as a stick, with all its normal benefits but inability to melee if in offhand. (range move lockout)
            javelins:
                very high power, medium defense and range, low speed and combo
                Have a specific launcher, the atlatl/woomera, which projects them further. Without a launcher they take significant time to charge.
                Pierces opponents like a spear, and pins them to the ground if possible.
                Unlike its melee variant, can be used in one hand, with the usual range move lockout.
            bolas and nets:
                very high defense, medium range and speed, low power and combo
                Very short charge time, auto-fires when charge is met.
                Entangles enemies, stopping their movement until they break out.
                Nets still entangle in melee, but bolas become similar to a whip or rope/chain weapon.
    ***Miscellaneous***
        ***spell casters (translation): keyword***
            fuchen (fly whisk): cleaning
            zhaohunfan (soul-drawing banner): soul reaping
            kusangbang (crying stick): death
            tuanshan (circular fans): wind
            yushan (feather fans): command
            books/scrolls: knowledge
            calligraphy pens: writing
            c a l a b a s h: storage
            gohei (miko staves): blessings

            etc etc etc...
 */