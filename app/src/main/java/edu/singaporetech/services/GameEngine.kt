package edu.singaporetech.services

interface OnGameEngineUpdate{
    fun GameLogicInit()
    fun PhysicsInit()
    fun OnPhysicsUpdate(dt : Float)
    fun OnGameLogicUpdate(dt : Float)
}

class GameEngine constructor( val updateInterval: Long, private val listener: OnGameEngineUpdate  ) {

    private var previousTime = System.currentTimeMillis()
    private var fpsTime = System.currentTimeMillis()
    private var deltaTime: Float = 0f
    fun getDeltaTime(): Float { return deltaTime }
    private var frames = 0
    private var FPS = 0
    fun getFPS(): Int { return FPS }
    private var FPSUpdated = false
    fun getFPSUpdated(): Boolean { return FPSUpdated }

    private var paused : Boolean = false
    fun setPaused(_Pause : Boolean) {paused = _Pause}
    fun getPaused() : Boolean {return paused}

    fun EngineInit() {
        SystemsInit()
    }
    fun EngineUpdate() {

        frames++
        deltaTime = (System.currentTimeMillis() - previousTime).toFloat()
        previousTime = System.currentTimeMillis()
        FPSUpdated = false

        if (System.currentTimeMillis() - fpsTime >= 1000) {
            //Log.d("Game:", "Game is Running at $frames fps")
            FPSUpdated = true
            FPS = frames
            frames = 0
            fpsTime = System.currentTimeMillis()
        }

        if(paused) return
        SystemsUpdate(deltaTime)
    }
    /*
    *
    * */
    private fun SystemsInit() {
        listener?.GameLogicInit()
        listener?.PhysicsInit()
    }

    /*
    *
    * */
    private fun SystemsUpdate(dt : Float) {
        listener?.OnGameLogicUpdate(dt)
        listener?.OnPhysicsUpdate(dt)
    }


}