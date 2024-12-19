"type": "palmon:production"

"focus_stat": "HP" // ame of the 6 stats: HP, ATTACK, DEFENCE, SPECIAL_ATTACK, SPECIAL_DEFENCE, SPEED
"min_level": 5, // minimum level required, 1-100
"required_type": "water", // name of the type of Pokemon, can be null
"ev_hp": 50, // minimum ev required, 0-255
"ev_atk": 50,
"ev_def": 0,
"ev_spa": 0,
"ev_spd": 0,
"ev_spe": 0,
"area_blocks": // json array of ingredients, should only be blocks, can be empty
[
{
"tag": "minecraft:logs"
},
{
"tag": "minecraft:planks"
}
],
"block_count": 4, // required number of each block, in this case requires 4 logs 4 planks, can be null
"tick": 400, // tick cost
"result_items": // result items, can be null
[
{
"item": "palmon:wood",
"count": 2
}
],
"result_power": 0, // generated power, 0 to somewhat number
"result_fluid": // result fluid stack, can be null
{
"fluid": "minecraft:water",
"amount": 1000
} 
