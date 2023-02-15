package edu.singaporetech.services


class Enemy(gameActivity: GameActivity) : Entity() {
    private val screenWidth: Float = (gameActivity.resources.displayMetrics.widthPixels).toFloat()
    private val screenHeight: Float = (gameActivity.resources.displayMetrics.heightPixels).toFloat()

    val shoot: Shoot = Shoot(gameActivity,1000F, 0.5F, screenHeight, true)

    private val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    init {
        position = Vector2(screenWidth / 2, screenHeight / 8)
        colliderScale = Vector2(200F, 100F)
        speed = 0.5F
        velocity.x = speed
        tag = "enemy"

        // Setting texture
        renderObject.setImageResource(R.drawable.coin)
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }


    fun updateShootMovement(dt: Float) {
        shoot.updateMovement(dt)
    }


    fun update(dt: Float) {
        if (getColliderMax().x >= screenWidth) {
            velocity.x = -speed
        } else if (getColliderMin().x <= 0F) {
            velocity.x = speed
        }

        // Update position
        renderObject.x = position.x
        renderObject.y = position.y

        // Shooting out projectile at delayed intervals
        shoot.update(dt, this, false)
    }
}
