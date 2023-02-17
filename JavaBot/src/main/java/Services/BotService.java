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
        System.out.println();
        System.out.println();
        System.out.println("UKURAN BOT : " + bot.getSize());
        System.out.println();

        var heading = 90;
        playerAction.action = PlayerActions.FORWARD;
        var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
        var teleporter = gameState.getGameObjects().stream().filter(tele -> tele.getGameObjectType() == ObjectTypes.TELEPORTER).sorted(Comparator.comparing(tele -> getDistanceBetween(bot, tele))).collect(Collectors.toList());
        System.out.println("TELEPORTER ============" + teleporter);


        System.out.println("KEJEPIT MAS :" + kejepitctr);
        if(!gameState.getGameObjects().isEmpty()){
            if(target == null || target == worldCenter)
            {
                System.out.println("NO CURRENT TARGET FOR TEST BOT");
                heading = resolveNewTarget();
            }
            else
            {
                var targetWithNewValues = gameState.getPlayerGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList()).isEmpty() ? gameState.getGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList()) : gameState.getPlayerGameObjects().stream().filter(go -> go.id == target.id).collect(Collectors.toList());
                // var targetWithNewValues =  gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
                if(targetWithNewValues == null || targetWithNewValues.isEmpty()){//targetWithNewValues == null || targetWithNewValues.isEmpty()
                    System.out.println("OLD TARGET INVALID, TEST BOT RESOLVING NEW TARGET");
                    heading = resolveNewTarget();
                    // if(targetWithNewValues == null){
                    //     System.out.println("TARGET WITH NEW VALUES NULL");
                    // }else{
                    //     System.out.println("TARGET WITH NEW VALUES EMPTY");
                    // }
                }
                else{
                    System.out.println("PREVIOUS TARGET EXIST, TEST BOT UPDATING RESOLUTION");
                    target = targetWithNewValues.get(0);

                    if(1.25*target.size < bot.getSize()){
                        heading = getHeadingBetween(target);
                        System.out.println("TARGET IS SMALLER THAN TEST BOT");
                        teletarget = false;
                        if(1.25*target.size < bot.getSize()){
                            if(bot.getSize() > 50 && telectr > 80){
                                System.out.println("NEMBAK TELEPORT ANJINGGGGGGGGGGGGGGGGGGG");
                                playerAction.action = PlayerActions.FIRETELEPORT;
                                // teleporter = gameState.getGameObjects().stream().filter(tele -> tele.getGameObjectType() == ObjectTypes.TELEPORTER).sorted(Comparator.comparing(tele -> getDistanceBetween(bot, tele))).collect(Collectors.toList());
                                
                                telectr = 0;
                            }
                        }
                        
                    }
                    else{
                        System.out.println("PREVIOUS TARGET LARGER, TEST BOT RESOLVING NEW TARGET");
                        heading = resolveNewTarget();
                    }
                }
            }
            // playerAction.heading = heading;
            if(!teleporter.isEmpty()){
                telekita = teleporter.get(0);
                teletarget = true;
                System.out.println("TELETARGET ============ TRUE");
            }
            GameObject worldC= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
            var distanceFromWorldCenter = getDistanceBetween(bot, worldC);
            if (distanceFromWorldCenter + (1.5 * bot.getSize()) > gameState.world.getRadius()){
                worldCenter=new GameObject(null, null, null, null, new Position(0, 0), null);
                // heading = getHeadingBetween(worldCenter);
                heading = getHeadingBetween(worldC);
                target = worldCenter;
                System.out.println("BOT AVOIDING VOID, MOVING TO CENTER");
                //udah1 = true;
                count++;
                kejepitctr++;
                }

            if (targetIsPlayer)
            {
                System.out.println("FIRING TORPEDOES AT TARGET");
                // System.out.println("FIRING TELEPORT AT TARGET");
                heading=getHeadingBetween(nearestPlayer.get(0));
                playerAction.action = PlayerActions.FIRETORPEDOES;
                // playerAction.action=PlayerActions.FIRE_TELEPORT;
            }

            if(kejepitctr > 10){
                heading = getHeadingBetween(worldC) + 90;
                System.out.println("KEJEPIT ANJINGGGG yang pertama");
                // kejepitctr = 0;
            }
            // } else if(kejepitctr > 5){
            //     heading = getHeadingBetween(worldC) + 90;
            //     System.out.println("KEJEPIT ANJINGGGG yang kedua");
            // }

            if(kejepitctr > 20){
                heading = getHeadingBetween(worldC);
                // heading = resolveNewTarget();
                temptick = gameState.world.getCurrentTick() + 10;
                playerAction.action = PlayerActions.FIRETELEPORT;
                nembaktele = true;
                kejepitctr = 0;
            }

            if(temptick == gameState.world.getCurrentTick()){
                System.out.println("BOT TELEPORT ANJING");
                playerAction.action = PlayerActions.TELEPORT;
                temptick = 0;
                nembaktele = false;
            }

           
            if(teletarget1){
                if(getDistanceBetween(telekita, nearestPlayer.get(0)) <= 75 + bot.getSize() / 2 && teletarget1){
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    System.out.println("============");
                    System.out.println("TELEPORT TO TARGET");
                    System.out.println("TELEPORT TO TARGET");
                    System.out.println("TELEPORT TO TARGET");
                    System.out.println("TELEPORT TO TARGET");
                    System.out.println("============");
                    System.out.println();
                    System.out.println();
                    System.out.println();
                    playerAction.action = PlayerActions.TELEPORT;
                    teletarget1 = false;
                    teletarget = false;
                }
            }

            if(teletarget && bot.size > 50){
                System.out.println("FIRING TELEPORT TO TARGET");
                heading = getHeadingBetween(nearestPlayer.get(0));
                playerAction.action = PlayerActions.FIRETELEPORT;
                teletarget = false;
                teletarget1 = true;
            }
            System.out.println("INI HEADING : " + heading);
            playerAction.heading = heading;
            System.out.println(telectr);
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
        System.out.println("COUNT : " + count);
        count = 0;
        if(p){
            return (int) (v * (180 / Math.PI) - 90);    
        }
        else{
            System.out.println("COUNT : " + 0);
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
        var neareestGasCloud = gameState.getGameObjects().stream().filter(gascloud -> gascloud.getGameObjectType() == ObjectTypes.GASCLOUD).sorted(Comparator.comparing(gascloud -> getDistanceBetween(bot, gascloud))).collect(Collectors.toList());
        var zupernova = gameState.getGameObjects().stream().filter(supernova -> supernova.getGameObjectType() == ObjectTypes.SUPERNOVAPICKUP).collect(Collectors.toList());
        //var nearestAsteroid = gameState.getGameObjects().stream().filter(asteroid -> asteroid.getGameObjectType() == ObjectTypes.ASTEROIDFIELD).sorted(Comparator.comparing(asteroid -> getDistanceBetween(bot, asteroid))).collect(Collectors.toList());          

        if(nearestPlayer.isEmpty()){
            System.out.println("Wa a a a");
        }
        var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));

        if(getDistanceBetween(nearestFood.get(0), bot) < 0.8*getDistanceBetween(nearestSuperFood.get(0), bot)){
            fixfood = nearestFood.get(0);
        } else {
            fixfood = nearestSuperFood.get(0);
        }

        if(getDistanceBetween(fixfood, bot) < getDistanceBetween(neareestGasCloud.get(0), bot) || (getDistanceBetween(nearestPlayer.get(0), bot) < getDistanceBetween(neareestGasCloud.get(0), bot))){

            //bot kita lebih kecil
            if(nearestPlayer.get(0).getSize() > bot.getSize()){
                System.out.println("=====");
                System.out.println(("UKURAN TARGET : " + nearestPlayer.get(0).getSize()));
                System.out.println(("UKURAN BOT : " + bot.getSize()));
                System.out.println(("JARAK TARGET DENGAN TEST : " + getDistanceBetween(nearestPlayer.get(0), bot)));
                System.out.println("=====");

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
                    System.out.println("TEST BOT KABUR");
                    heading = GetOppositeDirection(nearestPlayer.get(0), bot);
                    target = fixfood;
                    System.out.println("MUSUH HEADING KE : " + nearestPlayer.get(0).currentHeading);

                    // heading = direction2NearestFood;
                    if (nearestPlayer.get(0).getSize()<5*bot.getSize()){
                        targetIsPlayer=true;
                    }else{

                        targetIsPlayer = false;
                    }
                    System.out.println("INI OPPOSITE DIKEJAR" + heading);
                    //count++;
                    // kejepitctr++;
                } else if(!zupernova.isEmpty() && getDistanceBetween(zupernova.get(0), bot) < zuperzone){
                    heading = getHeadingBetween(zupernova.get(0));
                    target = fixfood;
                    targetIsPlayer = false;
                    System.out.println("************************************");
                    System.out.println("TEST BOT HEADING TO SUPER DUPER NOVA");
                    System.out.println("************************************");
                }
                else {
                    heading = getHeadingBetween(fixfood);
                    target = fixfood;
                    targetIsPlayer = false;
                    System.out.println("TEST BOT GOING FOR FEEDING 1");
                    // kejepitctr = 0;
                }
                
            }
            
            //bot kita lebih gede
            else if (nearestPlayer.get(0).getSize() < 1.25*bot.getSize()){
                udah = false;
                // udah1 = false;
                if(getDistanceBetween(neareestGasCloud.get(0), bot) < 3*bot.getSize()){
                    heading = GetOppositeDirection2(neareestGasCloud.get(0), bot);
                    System.out.println("TEST BOT CHASING TARGET BUT GAS CLOUD");
                }
                else{
                    if(getDistanceBetween(nearestPlayer.get(0), bot) < (4*bot.getSize())){
                        heading = direction2NearestPlayer;
                        target = nearestPlayer.get(0);
                        if(getDistanceBetween(nearestPlayer.get(0), bot) < 5 * bot.getSize())
                            targetIsPlayer = true;
                        System.out.println("TEST BOT CHASING SMALLER PLAYER");
                    }else {
                        heading = getHeadingBetween(fixfood);
                        target = fixfood;
                        targetIsPlayer = false;
                        System.out.println("TEST BOT GOING FOR FEEDING 2");
                        // kejepitctr = 0;
                    }
                }
            }
            else if(fixfood != null){
                heading = getHeadingBetween(fixfood);
                target = fixfood;
                targetIsPlayer = false;
                System.out.println("TEST BOT GOING FOR FEEDING 3");
                // kejepitctr = 0;
            }
            else{
                target = nearestPlayer.get(0);
                // heading = getHeadingBetween(worldCenter);
                GameObject worldC2= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
                heading = getHeadingBetween(worldC2);
                targetIsPlayer = false;
                System.out.println("TEST BOT COULDNT FIND ANYTHING 4");
                // kejepitctr = 0;
            }
        } else{//ini gascloud 
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
                System.out.println("ADA GAS CLOUD / ASTEROIDTAPI ADA TARGET, KABUR DARI PLAYER ");
            } else if (getDistanceBetween(neareestGasCloud.get(0), bot) < dangerzone2){
                heading = GetOppositeDirection2(neareestGasCloud.get(0), bot);
                target = fixfood;
                System.out.println("TEST NGEHINDAR GAS CLOUD / ASTEROID MASIH JAUH ");
            }
            else{
                heading = GetOppositeDirection2(neareestGasCloud.get(0), bot);
                target = nearestPlayer.get(0);
                System.out.println("TEST NGEHINDAR GAS CLOUD / ASTEROID");
                // kejepitctr++;
            }
            count++;
            
        }
        System.out.println("RETURN RESOLVE : " + heading);
        if(target == worldCenter){
            
            heading = getHeadingBetween(fixfood) ;
        }
        System.out.println("RETURN RESOLVE : " + heading);
        return heading;
    }


    private int GetAttackerResolution(GameObject bot, GameObject attacker, GameObject closestFood)
    {
        if(closestFood == null){
            return GetOppositeDirection(bot, attacker);
        }
        var distance2Attacker = getDistanceBetween(bot, attacker);
        var distanceBetweenAttackerAndFood = getDistanceBetween(attacker, closestFood);

        if(distance2Attacker > attacker.getSpeed() && distanceBetweenAttackerAndFood > distance2Attacker){
            System.out.println("ATK IS FAR, TEST BOT GOING FOR FOOD");
            return getHeadingBetween(closestFood);
        }
        else{
            System.out.println("TEST BOT RUNNING");
            return GetOppositeDirection(bot, attacker);
        }
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