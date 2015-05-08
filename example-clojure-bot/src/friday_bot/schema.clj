(ns friday-bot.schema
  (:require [schema.core :as s])
  (:import (java.util UUID)))

(s/defschema Position
  {:x s/Num
   :y s/Num
   })

(s/defschema Item
  {:price           s/Num
   :discountPercent s/Num
   :position Position
   :type            s/Str
   :isUsable        s/Bool
   })

(s/defschema Player
  {(s/optional-key :id)                 UUID
   :name                                s/Str
   :url                                 s/Str
   :score                               s/Num
   :position                            Position
   :money                               s/Num
   :state                               s/Str
   :timeInState                         s/Num
   :actionCount                         s/Num
   (s/optional-key :invalidActionCount) s/Num
   (s/optional-key :health)             s/Num
   :usableItems [Item]
   })

(s/defschema Game-Map
  {:width        s/Num
   :height       s/Num
   :maxItemCount s/Num
   :name         s/Str
   :tiles [s/Str]
   :exit Position
   })

(s/defschema ShootingLine
  {:fromPosition Position
   :toPosition Position
   :age          s/Num})

(s/defschema Game-State
  {:map     Game-Map
   :players [Player]
   :finishedPlayers [Player]
   :items [Item]
   :round s/Num
   :shootingLines [ShootingLine]
   })

(s/defschema Game-State-Changed
  {:reason                       s/Str
   :gameState                    Game-State
   (s/optional-key :playerState) Player
   :playerId                     UUID})
