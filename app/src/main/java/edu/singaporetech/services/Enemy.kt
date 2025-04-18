package edu.singaporetech.services

class Enemy(gameActivity: GameActivity) : Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    private val size: Float = screenWidth * 0.07f

    var projectileDamage: Int = 1
    var enemyProjectileSpeed: Float = 0.5f
    val shoot: Shoot = Shoot(gameActivity, 1000F, enemyProjectileSpeed,
                             screenHeight, true, ProjectileType.Enemy)

    private val renderObject: GameSquare = GameSquare(gameActivity)

    //Initializing the entity variables
    init {
        position = Vector2(screenWidth / 2, screenHeight / 5)
        colliderScale = Vector2(size, size)
        speed = 0.5F
        velocity.x = speed

        // Setting texture
        renderObject.setImageResource(R.drawable.enemy)
        renderObject.setRotate(180f)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }

    //Storing the enemy data into the database
    fun setDatabaseVariables(position_: Vector2, velocityX: Float, projectileDamage_: Int,
                             projectileDelay_: Float, projectileTimer_: Float, projectileVelocity_: Float,
                             isAutoShoot_: Boolean, powerUpTimer_: Float) {
        position = position_
        velocity.x = velocityX
        projectileDamage = projectileDamage_

        shoot.setDatabaseVariables(projectileDelay_, projectileTimer_, projectileVelocity_,
                                   isAutoShoot_, powerUpTimer_)
    }

    //Update the projectiles position
    fun updateProjectilesPosition(dt: Float) {
        shoot.updatePositions(dt)
    }

    //Update the Enemy projectiles speed
    fun updateEnemyProjectileSpeed(speed:Float)
    {
        enemyProjectileSpeed = speed
        shoot.updateSpeed(speed)
    }

    //Update the logic for the Enemy projectiles to shoot as well as the rendering of the Enemy
    fun update(dt: Float) {
        if (getColliderMax().x >= screenWidth) {
            velocity.x = -speed
        } else if (getColliderMin().x <= 0F) {
            velocity.x = speed
        }

        renderObject.x = position.x
        renderObject.y = position.y

        // Shooting out projectile at delayed intervals
        shoot.update(dt, this, false)
    }
}
