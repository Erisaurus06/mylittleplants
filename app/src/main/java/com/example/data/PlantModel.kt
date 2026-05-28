package com.example.data

data class Plant(
    val id: String,
    val name: String,
    val scientificName: String,
    val category: PlantCategory,
    val briefDescription: String,
    val origin: String,
    val sunlight: String,
    val wateringInstructions: String, // How to feed them (water)
    val feedingNeeds: String,        // How to feed them (fertilizer/nutrients)
    val toxicityWarning: String? = null,
    val benefits: String? = null,
    val funFact: String
)

enum class PlantCategory(val displayName: String, val icon: String) {
    EDIBLE("Edible Plants", "🍃"),
    POISONOUS("Poisonous Plants", "⚠️"),
    MEDICINAL("Medicinal Plants", "🧪"),
    TREE("Trees & Shrubbery", "🌳")
}

data class GlossaryTerm(
    val term: String,
    val pronunciation: String,
    val definition: String,
    val origin: String,
    val contextualImportance: String
)

object BotanicalData {
    val plants = listOf(
        // Edible Plants
        Plant(
            id = "edible_tomato",
            name = "Tomato",
            scientificName = "Solanum lycopersicum",
            category = PlantCategory.EDIBLE,
            briefDescription = "A warm-season perennial grown as an annual, prized for its sweet, juicy red or yellow fruits used widely in global culinary dishes.",
            origin = "Western South America & Central America",
            sunlight = "Full Direct Sunlight (6-8 hours daily)",
            wateringInstructions = "Water deeply at the base to keep soil consistently moist, but not waterlogged. Avoid wetting the leaves to prevent moisture-related blights.",
            feedingNeeds = "Feed with a high-phosphorus organic fertilizer once every 2 weeks during peak flowering and fruit-setting to promote sweet yields.",
            benefits = "Rich in Vitamin C, potassium, and the antioxidant Lycopene, linked to improved heart health.",
            funFact = "Botanically a fruit/berry, but legally and culinarian classified as a vegetable due to a historic US Supreme Court decision in 1893!"
        ),
        Plant(
            id = "edible_mint",
            name = "Peppermint",
            scientificName = "Mentha x piperita",
            category = PlantCategory.EDIBLE,
            briefDescription = "An extremely aromatic, fast-growing herbaceous perennial with square stems and serrated green leaves that can spread rapidly.",
            origin = "Europe & Middle East",
            sunlight = "Partial Shade to Full Sunlight",
            wateringInstructions = "Keep soil consistently damp. Mint thrives in moist riverbeds, so it tolerates slightly more dampness than typical herbs.",
            feedingNeeds = "Lightly top-dress with compost or organic seaweed liquid fertilizer once in early spring. Avoid over-fertilizing as it diminishes the natural oils.",
            benefits = "Soothes irritable digestive symptoms, mitigates tension headaches, and freshens breath instantly.",
            funFact = "Mint spreads via subterranean runners called stolons. If unchecked, a single mint seed can conquer an entire garden block!"
        ),
        Plant(
            id = "edible_rosemary",
            name = "Rosemary",
            scientificName = "Salvia rosmarinus",
            category = PlantCategory.EDIBLE,
            briefDescription = "A woody, extremely resilient evergreen shrub with aromatic, needle-like leaves and delicate light blue, purple, or white flowers.",
            origin = "Mediterranean Basin",
            sunlight = "Incredibly intense, full sunlight (8+ hours daily)",
            wateringInstructions = "Water sparingly! Rosemary is highly drought-tolerant. Only water when the soil feels completely dry 2 inches deep. Excellent drainage is essential.",
            feedingNeeds = "Requires almost no fertilizer. A very light top-dress of bone meal in the early spring is more than enough for abundant growth.",
            benefits = "Contains carnosic acid, a compound shown to support cognitive health, memory recall, and circulatory systems.",
            funFact = "In folklore, Rosemary represents remembrance; ancient Greek students wore sprigs of rosemary in their hair during final examinations."
        ),
        Plant(
            id = "edible_basil",
            name = "Sweet Basil",
            scientificName = "Ocimum basilicum",
            category = PlantCategory.EDIBLE,
            briefDescription = "A tender, lush annual herb of the mint family, featuring bright green rounded leaves that carry a sweet, peppery, anise-like flavor.",
            origin = "Tropical regions of Central Africa to Southeast Asia",
            sunlight = "Warm, bright indirect to full direct sunlight (6+ hours)",
            wateringInstructions = "Water regularly. Basil likes its root level kept slightly damp, so irrigate whenever the top 1 inch of soil begins to dry out.",
            feedingNeeds = "Feed with nitrogen-heavy liquid fertilizer once every 4 watering cycles to encourage continuous leaf production.",
            benefits = "Provides high amounts of Vitamin K and contains natural volatile oils that act as anti-inflammatory agents.",
            funFact = "Basil is known as the 'King of Herbs'—its botanical name contains 'basilikon', meaning 'royal' in Greek."
        ),

        // Poisonous Plants
        Plant(
            id = "poison_oleander",
            name = "Oleander",
            scientificName = "Nerium oleander",
            category = PlantCategory.POISONOUS,
            briefDescription = "A stunning evergreen ornamental shrub bearing cluster-form pink, red, or white blooms. However, every single cell is dangerously toxic.",
            origin = "Mediterranean and Subtropical Asia",
            sunlight = "Full intense sun. Extremely heat and drought resistant.",
            wateringInstructions = "Water deeply once every 10 days once established. Can survive long arid spells without structural distress.",
            feedingNeeds = "Apply standard low-nitrogen slow-release pellet in early spring. It has extremely basic soil needs and thrives in poor soil.",
            toxicityWarning = "Contains deadly cardiac glycosides (like oleandrin). Induces rapid cardiac arrhythmia, nausea, and can be fatal to humans or pets if even a tiny leaf is ingested.",
            funFact = "Even the smoke from burning dead oleander twigs carries toxic compounds that can irritate eyes, lungs, and skin."
        ),
        Plant(
            id = "poison_belladonna",
            name = "Deadly Nightshade",
            scientificName = "Atropa belladonna",
            category = PlantCategory.POISONOUS,
            briefDescription = "A branching perennial herb featuring dull purple trumpet-shaped blossoms and shiny, dark purple-black berries that look sweet but are lethal.",
            origin = "Europe, North Africa, and Western Asia",
            sunlight = "Dappled forest light, prefers partial woodsy shade.",
            wateringInstructions = "Prefers moist, lime-rich chalky soils. Water moderately to maintain natural woodland dampness.",
            feedingNeeds = "Does not require human fertilization. Thrives on the slow decay of humus in forest floors.",
            toxicityWarning = "Highly toxic tropane alkaloids (atropine, scopolamine, hyoscyamine). Ingestion leads to severe dry mouth, dilated pupils, delirium, and cardiac arrest.",
            funFact = "In the Renaissance, Italian women used dilated drops made of this plant to enlarge their pupils for aesthetic allure, naming it 'Bella Donna' ('Beautiful Lady')."
        ),
        Plant(
            id = "poison_foxglove",
            name = "Foxglove",
            scientificName = "Digitalis purpurea",
            category = PlantCategory.POISONOUS,
            briefDescription = "A tall, majestic biennial that shoots up a 2-5 foot spire of downwards-hanging, bell-shaped blossoms speckled pink and violet on the interior.",
            origin = "Western and Southwestern Europe",
            sunlight = "Partial morning sun or filtered forest canopy.",
            wateringInstructions = "Prefers light and consistently moist acidic soil. Water weekly, especially during dry midsummer spells.",
            feedingNeeds = "Requires rich soil. Incorporate balanced compost/humus around the crown base in late spring.",
            toxicityWarning = "Contains powerful digitoxin. Induces dramatic heartbeat fluctuations, blurred yellow-tinted vision, intense headaches, and lethal toxicity.",
            funFact = "While highly toxic, extracts from Digitalis purpurea are refined into medical Digoxin, used to save patients suffering congestive heart failure!"
        ),

        // Medicinal Plants
        Plant(
            id = "med_aloe",
            name = "Aloe Vera",
            scientificName = "Aloe vera",
            category = PlantCategory.MEDICINAL,
            briefDescription = "A stemless, succulent evergreen with thick, fleshy lanceolate leaves filled with a soothing, transparent gelatinous sap.",
            origin = "Arabian Peninsula",
            sunlight = "Bright indirect light. Sensitive to cold temperatures and intense sun sunburn.",
            wateringInstructions = "Allow the soil to dry fully between deep watering schedules. Adhere strictly to a 'soak-and-dry' method to prevent root decay.",
            feedingNeeds = "Succulent food or heavily diluted high-potassium fertilizer twice during the warm summer. Do not feed in winter.",
            benefits = "The interior mucilaginous gel cools sunburns, minor cuts, and reduces skin inflammation rapidly.",
            funFact = "In ancient Egyptian tombs, drawings of Aloe Vera were placed along the path to the Valley of Kings, known as 'The Plant of Immortality'."
        ),
        Plant(
            id = "med_chamomile",
            name = "German Chamomile",
            scientificName = "Matricaria chamomilla",
            category = PlantCategory.MEDICINAL,
            briefDescription = "A delicate, feather-leaved annual herb featuring cheerful daisy-like white-petaled florets with glowing golden centers and an apple-like fragrance.",
            origin = "Southern and Eastern Europe",
            sunlight = "Plenty of sun, but tolerates very light afternoon shade.",
            wateringInstructions = "Water regularly when young, but becomes drought-resistant once the taproot sets. Ensure loose, lightweight soil drainage.",
            feedingNeeds = "Over-fertilization produces lots of wispy leaves but very few high-potency medicinal flowers. Do not feed unless soil is entirely nutrient-depleted.",
            benefits = "Calms nervous systems, aids restful sleep cycles, eases muscle cramps, and acts as a skin-soothing tonic.",
            funFact = "The name Chamomile derives Greek 'chamaimelon', translating literally to 'ground apple' because of its sweet, fruity, apple-like scent when crushed."
        ),
        Plant(
            id = "med_lavender",
            name = "English Lavender",
            scientificName = "Lavandula angustifolia",
            category = PlantCategory.MEDICINAL,
            briefDescription = "A bushy, perennial subshrub with gray-green leaves and highly fragrant purple flower spikes that host potent essential oils.",
            origin = "Western Mediterranean region",
            sunlight = "Incredibly intense full sun (6-10 hours).",
            wateringInstructions = "Extremely drought-resistant. Never let the roots sit in water. Irrigate sparingly only when dry.",
            feedingNeeds = "Thrives in lean, sandy, alkaline soil. Avoid fertilizers, as excessive nitrogen results in poor aroma and soft wood.",
            benefits = "Aromathic oil reduces anxiety levels, supports skin recovery/burns, and decreases restlessness.",
            funFact = "The ancient Romans scented their bathwaters with lavender, deriving its title from 'lavare'—the Latin verb 'to wash'."
        ),

        // Trees
        Plant(
            id = "tree_oak",
            name = "White Oak",
            scientificName = "Quercus alba",
            category = PlantCategory.TREE,
            briefDescription = "A massive, extremely long-lived deciduous tree with spreading canopy limbs, deeply lobed pale leaves, and sweet acorns adored by wildlife.",
            origin = "Eastern North America",
            sunlight = "Full, unrestricted direct sun.",
            wateringInstructions = "Deep watering during dry summer months when young. Mature oaks have deep taproots and draw direct underground water.",
            feedingNeeds = "Feed young oaks yearly with compost around the drip-line. Avoid chemical feeds unless showing mineral deficiencies.",
            benefits = "A cornerstone ecological tree supporting hundreds of beneficial insect varieties, songbirds, and small mammals.",
            funFact = "White Oaks can live over 450 years! Their wood is completely waterproof, making them highly prized for wooden shipbuilding."
        ),
        Plant(
            id = "tree_japanese_maple",
            name = "Japanese Maple",
            scientificName = "Acer palmatum",
            category = PlantCategory.TREE,
            briefDescription = "A compact, highly graceful deciduous tree featuring intricate hand-shaped palmate leaves that turn a spectacular fiery crimson-red in autumn.",
            origin = "Japan, Korea, and Eastern China",
            sunlight = "Filtered light / Dappled shade. Hot afternoon sun can scorch the paper-thin colorful leaf tips.",
            wateringInstructions = "Requires consistent, balanced moisture. Water deep weekly, keeping the root ball cool with a heavy layer of organic mulch.",
            feedingNeeds = "A light slow-release food in late winter before new buds swell. Never feed maples heavily in summer.",
            benefits = "Brings deep zen-like tranquility, magnificent year-round color accents, and is a premier species for natural bonsai.",
            funFact = "Cultivated in Japan for centuries under the name 'Momiji', which means 'baby hands' due to the charming leaf structure."
        )
    )

    // Culinary & Botanical Glossary terms to simulate a dictionary of terms
    val glossary = listOf(
        GlossaryTerm(
            term = "Photosynthesis",
            pronunciation = "foh-toh-sin-thuh-sis",
            definition = "The process by which green plants and some other organisms use sunlight to synthesize nutrients from carbon dioxide and water, generating oxygen as a byproduct.",
            origin = "Greek 'phōs' (light) + 'sunthesis' (putting together)",
            contextualImportance = "It is the fundamental biochemical mechanism that fuels almost all life on Earth by converting raw solar energy into consumable plant starches."
        ),
        GlossaryTerm(
            term = "Chlorophyll",
            pronunciation = "klawr-uh-fil",
            definition = "A green, light-absorbing pigment found inside the chloroplasts of plants, algae, and cyanobacteria.",
            origin = "Greek 'khlōros' (pale green) + 'phullon' (leaf)",
            contextualImportance = "This pigment captures red and blue wave frequencies from solar light, transferring energetic electrons to fuel carbohydrate formation."
        ),
        GlossaryTerm(
            term = "Xylem",
            pronunciation = "zahy-lem",
            definition = "The vascular tissue in plants that conducts water and dissolved nutrients upward from the roots to the stems and leaves.",
            origin = "Greek 'xulon' (wood)",
            contextualImportance = "Acts as a unidirectional elevator, drawing groundwater upwards through capillary action and transpiration tension."
        ),
        GlossaryTerm(
            term = "Phloem",
            pronunciation = "floh-em",
            definition = "The living tissue in vascular plants that transports soluble organic compounds (like sucrose) made during photosynthesis downward and outward.",
            origin = "Greek 'phloios' (bark)",
            contextualImportance = "Functions as the plant's bidirectional circulatory network, distributing vital sugars, proteins, and chemical messengers."
        ),
        GlossaryTerm(
            term = "Deciduous",
            pronunciation = "dih-sij-oo-uhs",
            definition = "Shedding leaves annually at the end of the growing season, typically in autumn in temperate zones.",
            origin = "Latin 'decidere' (to fall off)",
            contextualImportance = "A strategic survival adaptation to conserve water and prevent timber breakage from freezing temperatures."
        ),
        GlossaryTerm(
            term = "Angiosperm",
            pronunciation = "an-jee-uh-spurm",
            definition = "A plant that produces flowers and bears its seeds protected inside a carpel or hollow ovary (fruits).",
            origin = "Greek 'angeion' (vessel) + 'sperma' (seed)",
            contextualImportance = "Comprises over 80% of all land plant species, relying on co-evolved animal pollinators to achieve high reproductive success."
        ),
        GlossaryTerm(
            term = "Herbaceous",
            pronunciation = "hur-bey-shuhs",
            definition = "Plants with soft, flexible green stems that die back to the ground in winter, unlike woody plants with hard, persistent bark.",
            origin = "Latin 'herbaceus' (grassy/green)",
            contextualImportance = "They typically grow extremely rapidly, focusing their lifecycle energy on leaf production rather than woody structural longevity."
        ),
        GlossaryTerm(
            term = "Cotyledon",
            pronunciation = "kot-l-eed-n",
            definition = "An embryonic leaf in seed-bearing plants, one or more of which are the first leaves to appear from a germinating seed.",
            origin = "Greek 'kotylēdōn' (cup-shaped hollow)",
            contextualImportance = "Provides initial photosynthesis and stored nourishment to the developing seedling before true foliage takes over."
        )
    )
}
