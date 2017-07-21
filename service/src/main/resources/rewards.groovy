onConsume = {
    reward ( "Watch a Pixar Movie, get 25% extra points." ) {
        allOf {
            condition {
                media.publisher == "Disney"
            }
            condition {
                isVideo
            }
        }

        grant {
            points media.points / 4
        }
    }
    reward ( "Rent a new release, get extra night rental" ) {
        condition {
            isNewRelease
        }

        grant {
            extend 1
        }
    }
}

onPurchase = {
    reward ( "Earn 10% bonus points on all games." ) {
        condition {
            isGame
        }
        grant {
            points media.points / 10
        }
    }
}

onUpgrade = {
    reward ("Upgrade to PLUS and get 100 free points") {
        condition {
            toPlan == "PLUS"
        }
        grant {
            points 100
        }
    }
}