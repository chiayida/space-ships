package edu.singaporetech.services

enum class ProjectileType{
    Player,
    Enemy,
    DamageBoost,
    AddHealth,
    Sheild
}

class Projectile(gameActivity: GameActivity,
                 shooterPosition: Vector2, _velocity: Float,
                 private val projectileBoundary: Float, type : ProjectileType): Entity() {
    private var flag: Boolean = _velocity > 0F
    val renderObject: GameGLSquare = GameGLSquare(gameActivity)

    val projType = type
    init {
        position = Vector2(shooterPosition.x, shooterPosition.y)
        colliderScale = Vector2(35F, 35F)
        speed = _velocity
        velocity.y = speed

        // Setting texture
        if(type == ProjectileType.Player){
            renderObject.setImageResource(R.drawable.player_bullet)
        }
        else if(type == ProjectileType.Enemy){
            renderObject.setImageResource(R.drawable.enemy_bullet)
        }
        else if(type == ProjectileType.DamageBoost)
        {
            colliderScale = Vector2(50F, 50F)
            renderObject.setImageResource(R.drawable.player)
        }
        else if(type == ProjectileType.AddHealth)
        {
            colliderScale = Vector2(50F, 50F)
            renderObject.setImageResource(R.drawable.enemy)
        }
        else if(type == ProjectileType.Sheild)
        {
            colliderScale = Vector2(50F, 50F)
            renderObject.setImageResource(R.drawable.coin)
        }
        renderObject.xScale = colliderScale.x
        renderObject.yScale = colliderScale.y
    }

    fun getProjectileType():ProjectileType
    {
        return projType
    }
    fun update(): Boolean {
        if ((getColliderMin().y >= projectileBoundary && flag) ||
            (getColliderMax().y <= projectileBoundary && !flag)) {
            GameGLSquare.toBeDeleted.add(renderObject)
            return false
        }

        // Update position
        renderObject.x = position.x
        renderObject.y = position.y

        return true
    }
}