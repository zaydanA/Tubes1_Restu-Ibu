package Services;

import Enums.*;
import Models.*;
import io.reactivex.internal.schedulers.NewThreadScheduler;

import java.util.*;
import java.util.stream.*;

public class BotService {
    private GameObject bot;
    private PlayerAction playerAction;
    private GameState gameState;

    private GameObject target;
    private boolean targetIsPlayer = false; 
    private boolean shieldactive = false;
    private GameObject worldCenter;
    private boolean udah=false;
    private int kejepitctr = 0;
    private boolean nembaktele=false;
    private int count=0;
    private int temptick;
    private boolean teletarget;
    private boolean teletarget1=false;
    private GameObject telekita;
    private int telectr =0;
    private int supernovactr = 0;
    private boolean issupernova = false;
    private int tempticksupernova =0;


    public BotService() {
        this.playerAction = new PlayerAction();
        this.gameState = new GameState();
    }


    public GameObject getBot() {
        return this.bot;
    }

    public void setBot(GameObject bot) {
        this.bot = bot;
    }

    public PlayerAction getPlayerAction() {
        return this.playerAction;
    }

    public void setPlayerAction(PlayerAction playerAction) {
        this.playerAction = playerAction;
    }

    public void computeNextPlayerAction(PlayerAction playerAction) {


        var heading = 90;
        playerAction.action = PlayerActions.FORWARD;
        var nearesttorpedo = gameState.getGameObjects().stream().filter(torpedo -> torpedo.getGameObjectType() == ObjectTypes.TORPEDOSALVADO).collect(Collectors.toList());
        var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
        var teleporter = gameState.getGameObjects().stream().filter(tele -> tele.getGameObjectType() == ObjectTypes.TELEPORTER).sorted(Comparator.comparing(tele -> getDistanceBetween(bot, tele))).collect(Collectors.toList());
        if(!gameState.getGameObjects().isEmpty()){
            if(target == null || target == worldCenter)
            {
                heading = resolveNewTarget();
            }
            else
            {
                var targetWithNewValues = gameState.getPlayerGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList()).isEmpty() ? gameState.getGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList()) : gameState.getPlayerGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList());
                
                if(targetWithNewValues == null || targetWithNewValues.isEmpty()){//targetWithNewValues == null || targetWithNewValues.isEmpty()
                
                    heading = resolveNewTarget();

                }
                else{
                    target = targetWithNewValues.get(0);

                    if(1.25*target.size < bot.getSize()){
                        heading = getHeadingBetween(target);
                        
                        teletarget = false;
                        
                    }
                    else{
                        ;
                        heading = resolveNewTarget();
                    }
                }
            }
            // playerAction.heading = heading;
            if(!teleporter.isEmpty() && bot.getSize() > 99 && telectr > 120){
                telekita = teleporter.get(0);
                teletarget = true;
                
                telectr=0;
            }
            GameObject worldC= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
            var distanceFromWorldCenter = getDistanceBetween(bot, worldC);
            if (distanceFromWorldCenter + (1.5 * bot.getSize()) > gameState.world.getRadius()){
                worldCenter=new GameObject(null, null, null, null, new Position(0, 0), null);
               
                heading = getHeadingBetween(worldC);
                target = worldCenter;
                
                count++;
                kejepitctr++;
                }

            if (targetIsPlayer)
            {

                heading=getHeadingBetween(nearestPlayer.get(0));
                playerAction.action = PlayerActions.FIRETORPEDOES;
            
            }

            if(kejepitctr > 10){
                heading = getHeadingBetween(worldC) + 90;

            }


            if(kejepitctr > 20){
                heading = getHeadingBetween(worldC);
                // heading = resolveNewTarget();
                temptick = gameState.world.getCurrentTick() + 10;
                playerAction.action = PlayerActions.FIRETELEPORT;
                nembaktele = true;
                kejepitctr = 0;
            }

            if(temptick == gameState.world.getCurrentTick() && getDistanceBetween(nearestPlayer.get(0), bot) > 50){

                playerAction.action = PlayerActions.TELEPORT;
                temptick = 0;
                nembaktele = false;
            }
           
            if(teletarget1){
                if(getDistanceBetween(telekita, nearestPlayer.get(0)) <= 75 + bot.getSize() / 2 && teletarget1 && (1.75*nearestPlayer.get(0).getSize() < bot.getSize())){

                    playerAction.action = PlayerActions.TELEPORT;
                    teletarget1 = false;
                    teletarget = false;
                }
            }

            if(teletarget && bot.size > 120 ){

                heading = getHeadingBetween(nearestPlayer.get(0));
                playerAction.action = PlayerActions.FIRETELEPORT;
                teletarget = false;
                teletarget1 = true;
                telectr=0;
            }
            if(!nearesttorpedo.isEmpty()){
                if (getDistanceBetween(nearesttorpedo.get(0), bot) < 2*bot.getSize() && bot.getSize() > 60){

                    shieldactive = true;
                }
            }
            if(shieldactive){
                playerAction.action = PlayerActions.ACTIVATESHIELD;
                shieldactive = false;
            }

            if(bot.supernovaAvailable > 0){

                playerAction.action = PlayerActions.FIRESUPERNOVA;
                playerAction.heading = getHeadingBetween(nearestPlayer.get(0));
                issupernova = true;
                tempticksupernova = gameState.world.getCurrentTick() + 20;
            }

            if(issupernova){
                supernovactr++;
            }

            if(tempticksupernova == gameState.world.getCurrentTick()){
                playerAction.action = PlayerActions.DETONATESUPVERNOVA;
                issupernova = false;
            }


            playerAction.heading = heading;

            telectr++;
            System.out.println("TEST BOT ACTION : " + playerAction.action + " : " + playerAction.heading);
            this.playerAction = playerAction;
        }
    }
    public GameState getGameState() {
        return this.gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        updateSelfState();
    }

    private void updateSelfState() {
        Optional<GameObject> optionalBot = gameState.getPlayerGameObjects().stream().filter(gameObject -> gameObject.id.equals(bot.id)).findAny();
        optionalBot.ifPresent(bot -> this.bot = bot);
    }

    private double getDistanceBetween(GameObject object1, GameObject object2) {
        var triangleX = Math.abs(object1.getPosition().x - object2.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - object2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int getHeadingBetween(GameObject otherObject) {
        var direction = toDegrees(Math.atan2(otherObject.getPosition().y - bot.getPosition().y,
                otherObject.getPosition().x - bot.getPosition().x));
        return (direction + 360) % 360;
    }

    private int toDegrees(double v) {
        return (int) (v * (180 / Math.PI));
    }

    private int toDegrees2(double v, boolean p) {

        count = 0;
        if(p){
            return (int) (v * (180 / Math.PI) - 90);    
        }
        else{

            return toDegrees(v);
        }
    }

    private int resolveNewTarget()
    {
        int heading;
        GameObject fixfood;
        var nearestFood = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD).sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
        var nearestSuperFood = gameState.getGameObjects().stream().filter(superfood -> superfood.getGameObjectType() == ObjectTypes.SUPERFOOD).sorted(Comparator.comparing(superfood -> getDistanceBetween(bot, superfood))).collect(Collectors.toList());
        // var nearestFood1 = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD).collect(Collectors.toList());
        var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
        var nearestGasCloud = gameState.getGameObjects().stream().filter(gascloud -> gascloud.getGameObjectType() == ObjectTypes.GASCLOUD).sorted(Comparator.comparing(gascloud -> getDistanceBetween(bot, gascloud))).collect(Collectors.toList());
        var nearestSupernova = gameState.getGameObjects().stream().filter(supernova -> supernova.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP).collect(Collectors.toList());
       
        //var nearestAsteroid = gameState.getGameObjects().stream().filter(asteroid -> asteroid.getGameObjectType() == ObjectTypes.ASTEROIDFIELD).sorted(Comparator.comparing(asteroid -> getDistanceBetween(bot, asteroid))).collect(Collectors.toList());          

        if(nearestPlayer.isEmpty()){
            System.out.println("WIN!");
        }
        var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));

        if(getDistanceBetween(nearestFood.get(0), bot) < 0.8*getDistanceBetween(nearestSuperFood.get(0), bot)){
            fixfood = nearestFood.get(0);
        } else {
            fixfood = nearestSuperFood.get(0);
        }

        if(getDistanceBetween(fixfood, bot) < getDistanceBetween(nearestGasCloud.get(0), bot) || (getDistanceBetween(nearestPlayer.get(0), bot) < getDistanceBetween(nearestGasCloud.get(0), bot))){

            //bot kita lebih kecil
            if(nearestPlayer.get(0).getSize() > bot.getSize()){

                double dangerzone = 0, zuperzone=0;

                if (nearestPlayer.get(0).getSize() > 150){
                    dangerzone = 1.5*nearestPlayer.get(0).getSize();
                } else if (nearestPlayer.get(0).getSize() > 75){
                    dangerzone = 4*nearestPlayer.get(0).getSize();
                } else if (nearestPlayer.get(0).getSize() > 0){
                    dangerzone = 6*nearestPlayer.get(0).getSize();
                }

                if(bot.getSize() > 150){
                    zuperzone = 2.5*bot.getSize();
                } else if(bot.getSize() > 75){
                    zuperzone = 5*bot.getSize();
                } else if(bot.getSize() > 0){
                    zuperzone = 7*bot.getSize();
                }
                
                if(getDistanceBetween(nearestPlayer.get(0), bot) < dangerzone){
                    udah = false;

                    heading = GetOppositeDirection(nearestPlayer.get(0), bot);
                    target = fixfood;
                    

                    // heading = direction2NearestFood;
                    if (nearestPlayer.get(0).getSize()<5*bot.getSize()){
                        targetIsPlayer=true;
                    }else {
                        targetIsPlayer = false;
                    }

                
                    //count++;
                    // kejepitctr++;
                } else if(!nearestSupernova.isEmpty() && getDistanceBetween(nearestSupernova.get(0), bot) < zuperzone){
                    heading = getHeadingBetween(nearestSupernova.get(0));
                    target = fixfood;
                    targetIsPlayer = false;

                }
                else {
                    heading = getHeadingBetween(fixfood);
                    target = fixfood;
                    targetIsPlayer = false;

                    // kejepitctr = 0;
                }
                
            }
            
            //bot kita lebih gede
            else if (nearestPlayer.get(0).getSize() < 1.25*bot.getSize()){
                shieldactive = false;
                udah = false;
                // udah1 = false;
                if(getDistanceBetween(nearestGasCloud.get(0), bot) < 3*bot.getSize()){
                    heading = GetOppositeDirection2(nearestGasCloud.get(0), bot);

                }
                else{
                    if(getDistanceBetween(nearestPlayer.get(0), bot) < (4*bot.getSize())){
                        heading = direction2NearestPlayer;
                        target = nearestPlayer.get(0);
                        if(getDistanceBetween(nearestPlayer.get(0), bot) < 5 * bot.getSize())
                            targetIsPlayer = true;

                    }else {
                        heading = getHeadingBetween(fixfood);
                        target = fixfood;
                        targetIsPlayer = false;

                        // kejepitctr = 0;
                    }
                }
            }
            else if(fixfood != null){
                shieldactive = false;
                heading = getHeadingBetween(fixfood);
                target = fixfood;
                targetIsPlayer = false;

                // kejepitctr = 0;
            }
            else{
                shieldactive = false;
                target = nearestPlayer.get(0);
                // heading = getHeadingBetween(worldCenter);
                GameObject worldC2= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
                heading = getHeadingBetween(worldC2);
                targetIsPlayer = false;

                // kejepitctr = 0;
            }
        } else{//ini gascloud 
            shieldactive = false;
            int dangerzone1 = 0;
            double dangerzone2 = 0;

            if (nearestPlayer.get(0).getSize() > 150){
                dangerzone1 = 2*bot.getSize();
                dangerzone2 = 0.5*bot.getSize();
            } else if (nearestPlayer.get(0).getSize() > 75){
                dangerzone1 = 4*bot.getSize();
                dangerzone2 = 1.5*bot.getSize();
            } else if (nearestPlayer.get(0).getSize() > 0){
                dangerzone1 = 8*bot.getSize();
                dangerzone2 = 3*bot.getSize();
            }
            if (getDistanceBetween(nearestPlayer.get(0), bot) < (dangerzone1)){ //8*botsize
                heading = GetOppositeDirection2(nearestPlayer.get(0), bot);
                target = fixfood;
                
            } else if (getDistanceBetween(nearestGasCloud.get(0), bot) < dangerzone2){
                heading = GetOppositeDirection2(nearestGasCloud.get(0), bot);
                target = fixfood;
                
            }
            else{
                heading = GetOppositeDirection2(nearestGasCloud.get(0), bot);
                target = nearestPlayer.get(0);


            }
            count++;
            
        }

        if(target == worldCenter){
            
            heading = getHeadingBetween(fixfood) ;
        }

        return heading;
    }

    private int GetOppositeDirection(GameObject gameObject1, GameObject gameObject2)
    {
        return toDegrees(Math.atan2(gameObject2.getPosition().y - gameObject1.getPosition().y, gameObject2.getPosition().x - gameObject1.getPosition().x));
    }

    private int GetOppositeDirection2(GameObject gameObject1, GameObject gameObject2)
    {
        if(count > 1){
            udah = true;
        }
        return toDegrees2(Math.atan2(gameObject2.getPosition().y - gameObject1.getPosition().y, gameObject2.getPosition().x - gameObject1.getPosition().x), udah);
    }

}