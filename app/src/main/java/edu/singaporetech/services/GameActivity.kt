package edu.singaporetech.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import edu.singaporetech.services.databinding.ActivityGameBinding


class GameActivity : AppCompatActivity(), SensorEventListener, OnGameEngineUpdate {

    val TAG: String = "GameActivity"
    private lateinit var binding: ActivityGameBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gamePlayer: Player
    private lateinit var gameEnemy: Enemy
    private var Enemies: MutableList<Enemy> = mutableListOf()

    private var FPSCap = 1L
    private var engine = GameEngine(FPSCap, this)

    private val handler = Handler()

    private lateinit var fpsView: TextView
    private lateinit var dtView: TextView
    private var direction:Float = 0.0f
    private var offsetBottom:Float = 250.0f

    private var screenWidth: Float = 0F
    private var screenHeight: Float = 0f

    var directionSpeed:Float = 1.5f
    var currentOrientation:Float = 0.0f
    private var isShoot: Boolean = false

    companion object {
        var screenWidth: Float = 0F
        var halfScreenWidth: Float = 0F
        var screenHeight: Float = 0F
        var halfScreenHeight: Float = 0F
    }

    private val updateRunnable = object : Runnable {

        override fun run() {
            // Perform tasks here when the activity is updated
            engine.EngineUpdate()

            if (engine.getFPSUpdated()) {
                fpsView?.text = "${engine.getFPS()} FPS"
                dtView?.text = "${engine.getDeltaTime()}ms dt"
            }

            handler.postDelayed(this, engine.updateInterval)
        }
    }

    private lateinit var gLView: GameGLSurfaceView
    /*
    *
    * */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        screenWidth = (resources.displayMetrics.widthPixels).toFloat()
        halfScreenWidth = screenWidth / 2F
        screenHeight = (resources.displayMetrics.heightPixels).toFloat()
        halfScreenHeight = screenHeight / 2F


        // Initialize view binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        gLView = GameGLSurfaceView(this)
        //setContentView(binding.root)
        setContentView(gLView)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // FIND ALL SCREEN OBJECTS

        //val tfpsView = TextView(this)
        fpsView = TextView(this)
        fpsView.text = "Hello, world!"
        fpsView.textSize = 24f
        addContentView(fpsView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        //val tdtView = TextView(this)
        dtView = TextView(this)
        dtView.text = " delta time"
        dtView.textSize = 24f
        dtView.y = 100f
        addContentView(dtView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))

        //fpsView =   binding.textViewFPS
        //dtView =  binding.textViewDeltaTime

        Log.d(TAG,resources.displayMetrics.heightPixels.toFloat().toString())

        screenWidth = resources.displayMetrics.widthPixels.toFloat()
        screenHeight = resources.displayMetrics.widthPixels.toFloat()

        gamePlayer = Player(this)
        gameEnemy = Enemy(this)

        direction = 0F


        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            Log.d(TAG,"Screen is tapped")

            isShoot = true
        }

        engine.EngineInit()
    }

    /*
    *
    * */
    override fun onResume() {
        super.onResume()
        // Register the listener for the gyroscope sensor
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        handler.postDelayed(updateRunnable, engine.updateInterval)
    }

    /*
    *
    * */
    override fun onPause() {
        super.onPause()
        // Unregister the listener
        sensorManager.unregisterListener(this)
        handler.removeCallbacks(updateRunnable)
    }

    /*
    *
    * */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.i("Mouse","X: ${event.x}, Y: ${event.y} ")
        val touchX = event.x
        val touchY = event.y
        gamePlayer.updatePosition(touchX, touchY)
        return super.onTouchEvent(event)
    }

    /*
    *
    * */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Get the three values for the gyroscope
            val x = event.values[0]
            val y = event.values[1]
            val deltaOrientationY = y * event.timestamp
            currentOrientation += deltaOrientationY
            /*Log.d(TAG,currentOrientation.toString())*/
            // Shift the game obj base on Y rot which is the x direction
            if(currentOrientation > 0.1)
            {
                direction = directionSpeed
            }
            else if(currentOrientation < -0.1)
            {
                direction = -directionSpeed
            }
            else
            {
                direction = 0.0f
            }
        }
    }

    /*
    *
    * */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.w(TAG, "Sensor accuracy changed to UNRELIABLE")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) {
            Log.w(TAG, "Sensor accuracy changed to LOW")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM) {
            Log.d(TAG, "Sensor accuracy changed to MEDIUM")
        } else if (accuracy == SensorManager.SENSOR_STATUS_ACCURACY_HIGH) {
            Log.d(TAG, "Sensor accuracy changed to HIGH")
        }
    }

    override fun GameLogicInit(){
        Enemies.add(gameEnemy)
    }


    override fun PhysicsInit(){

    }


    override fun OnPhysicsUpdate(dt : Float){
        // COLLISION CHECK
        for(k in Enemies.indices) {
            for(projectile in Enemies[k].shoot.projectiles){
                var projMIN = Vector2(projectile.getColliderMin().x , projectile.getColliderMin().y)
                var projMAX = Vector2(projectile.getColliderMax().x , projectile.getColliderMax().y)
                var projAABB = AABB(projMIN, projMAX)

                var playerMIN = Vector2(gamePlayer.getColliderMin().x , gamePlayer.getColliderMin().y)
                var playerMAX = Vector2(gamePlayer.getColliderMax().x , gamePlayer.getColliderMax().y)
                var playerAABB = AABB(playerMIN, playerMAX)

//                Log.i("lol", "Player Scale: ${gamePlayer.colliderScale.VectoString()} " +
//                    "Player Min: ${playerMIN.VectoString()}" +
//                        " , Max: ${playerMAX.VectoString()}")

                if(Physics.collisionIntersectionRectRect(projAABB, projectile.velocity, playerAABB, gamePlayer.velocity, dt)){
                    Log.i("lol", "Player got hit")
                }
            }
        }
        for(projectile in gamePlayer.shoot.projectiles){
            var projMIN = Vector2(projectile.getColliderMin().x , projectile.getColliderMin().y)
            var projMAX = Vector2(projectile.getColliderMax().x , projectile.getColliderMax().y)
            var projAABB = AABB(projMIN, projMAX)
            // else if it is the player's projectile, check collision with All ENEMIES
            for(j in Enemies.indices) {
                var enemyMIN = Vector2(Enemies[j].getColliderMin().x , Enemies[j].getColliderMin().y)
                var enemyMAX = Vector2(Enemies[j].getColliderMax().x , Enemies[j].getColliderMax().y)
                var enemyAABB = AABB(enemyMIN, enemyMAX)
                if(Physics.collisionIntersectionRectRect(projAABB, projectile.velocity,
                        enemyAABB, Enemies[j].velocity, dt)){
                    Log.i("lol", "Enemy got hit")
                }
            }
        }

        // MOVEMENT UPDATE
        gamePlayer.updateShootMovement(dt)
        for(i in Enemies.indices) {
            Enemies[i].updateShootMovement(dt)
            Enemies[i].updatePosition(dt)
        }
        gamePlayer.updatePosition(gamePlayer.position.x + direction
            ,resources.displayMetrics.heightPixels.toFloat() - offsetBottom)
    }


    override fun OnGameLogicUpdate(dt : Float){
        gameEnemy.update(dt)

        var entity = Entity()
        entity.position.x = gamePlayer.position.x - 50f
        entity.position.y = gamePlayer.position.y

        gamePlayer.update(dt, isShoot)
        isShoot = false
    }
}