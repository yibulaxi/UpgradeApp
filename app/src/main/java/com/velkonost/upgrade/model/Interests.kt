package com.velkonost.upgrade.model

import com.velkonost.upgrade.R

interface Interest {
    val id: Int

     val nameRes: Int
     val descriptionRes: Int
     val shortDescriptionRes: Int

     var selectedValue: Float

     val logo: Int

     companion object : Interest {
         override val id = 0
         override val nameRes = R.string.welcome_title
         override val descriptionRes = R.string.welcome_description
         override val shortDescriptionRes = R.string.welcome_short

         override var selectedValue: Float = 5f

         override val logo = R.drawable.logo

         fun getInterestById(id: Int): Interest {
             when(id) {
                 1 -> return Work()
                 2 -> return Spirit()
                 3 -> return Chill()
                 4 -> return Relationship()
                 5 -> return Health()
                 6 -> return Finance()
                 7 -> return Environment()
                 8 -> return Creation()
             }
             return this
         }
     }
}

 class Work : Interest {
     override val id = 1
     override val nameRes = R.string.work_title
     override val descriptionRes = R.string.work_description
     override val shortDescriptionRes = R.string.work_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.work_logo
}

 class Spirit : Interest {
     override val id = 2
     override val nameRes = R.string.spirit_title
     override val descriptionRes = R.string.spirit_description
     override val shortDescriptionRes = R.string.spirit_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.spirit_logo
}

 class Chill : Interest {
     override val id = 3
     override val nameRes = R.string.chill_title
     override val descriptionRes = R.string.chill_description
     override val shortDescriptionRes = R.string.chill_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.chill_logo
}

 class Relationship : Interest {
     override val id = 4
     override val nameRes = R.string.relationship_title
     override val descriptionRes = R.string.relationship_description
     override val shortDescriptionRes = R.string.relationship_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.relationship_logo
}

 class Health : Interest {
     override val id = 5
     override val nameRes = R.string.health_title
     override val descriptionRes = R.string.health_description
     override val shortDescriptionRes = R.string.health_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.health_logo
}

 class Finance : Interest {
     override val id = 6
     override val nameRes = R.string.finance_title
     override val descriptionRes = R.string.finance_description
     override val shortDescriptionRes = R.string.finance_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.finance_logo
}

 class Environment : Interest {
     override val id = 7
     override val nameRes = R.string.environment_title
     override val descriptionRes = R.string.environment_description
     override val shortDescriptionRes = R.string.environment_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.environment_logo
}

 class Creation : Interest {
     override val id = 8
     override val nameRes = R.string.creation_title
     override val descriptionRes = R.string.creation_description
     override val shortDescriptionRes = R.string.creation_short

     override var selectedValue: Float = 5f

     override val logo = R.drawable.creation_logo
}