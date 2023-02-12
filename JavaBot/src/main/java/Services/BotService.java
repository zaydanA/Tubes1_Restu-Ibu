package Services;

import Enums.*;
import Models.*;

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
    private int count=0;


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
        // playerAction.heading = new Random().nextInt(360);

        // if (!gameState.getGameObjects().isEmpty()) {
        //     var foodList = gameState.getGameObjects().stream().filter(item -> item.getGameObjectType() == ObjectTypes.FOOD).sorted(Comparator.comparing(item -> getDistanceBetween(bot, item))).collect(Collectors.toList());
            
        //     var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
        //     // if(!nearestPlayer.isEmpty()){
        //     //      System.out.println(nearestPlayer.get(0));
        //     // }
        //     // if(!nearestPlayer.isEmpty()){
        //     //     playerAction.heading = getHeadingBetween(foodList.get(0));
        //     // }
        //     playerAction.heading = 90;
        //     this.playerAction = playerAction;
        // }

        //System.out.println("TEST BOT ACTION : " + playerAction.action + " : " + playerAction.heading);
        if(!gameState.getGameObjects().isEmpty()){
            if(target == null || target == worldCenter)
            {
                System.out.println("NO CURRENT TARGET FOR TEST BOT");
                var nearestPlayer = gameState.getPlayerGameObjects().stream().filter(target -> target.id != bot.id).sorted(Comparator.comparing(target -> getDistanceBetween(bot, target))).collect(Collectors.toList());
                if(!nearestPlayer.isEmpty()){
                }
                heading = resolveNewTarget();
            }
            else
            {
                var targetWithNewValues = gameState.getPlayerGameObjects().stream().filter(go -> go.id != target.id).collect(Collectors.toList());
                
                if(targetWithNewValues == null){
                    System.out.println("OLD TARGET INVALID, TEST BOT RESOLVING NEW TARGET");
                    heading = resolveNewTarget();
                }
                else{
                    System.out.println("PREVIOUS TARGET EXIST, TEST BOT UPDATING RESOLUTION");
                    target = targetWithNewValues.get(0);

                    if(target.size < bot.getSize()){
                        heading = getHeadingBetween(target);
                        System.out.println("TARGET IS SMALLER THAN TEST BOT");
                    }
                    else{
                        System.out.println("PREVIOUS TARGET LARGER, TEST BOT RESOLVING NEW TARGET");
                        heading = resolveNewTarget();
                    }
                }
            }
            // playerAction.heading = heading;
        
            GameObject worldC= new GameObject(null, null, null, null, gameState.world.getCenterPoint(), null);
            var distanceFromWorldCenter = getDistanceBetween(bot, worldC);
            if (distanceFromWorldCenter + (1.5 * bot.getSize()) > gameState.world.getRadius()){
                worldCenter=new GameObject(null, null, null, null, new Position(0, 0), null);
                heading = getHeadingBetween(worldCenter);
                target = worldCenter;
                System.out.println("BOT AVOIDING VOID, MOVING TO CENTER");
                }

            if (targetIsPlayer)
            {
                System.out.println("FIRING TORPEDOES AT TARGET");
                playerAction.action = PlayerActions.FIRETORPEDOES;
            }
            System.out.println("INI HEADING : " + heading);
            playerAction.heading = heading;
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

        // if(!nearestPlayer.isEmpty()){
        //     var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));
        //     System.out.println(direction2NearestPlayer);
        //     System.out.println("PEMBATAS PLAYER");
        // }
        // if(!nearestFood.isEmpty()){
        //     var direction2NearestFood = getHeadingBetween(nearestFood.get(0));
        //     System.out.println(direction2NearestFood);
        //     System.out.println("PEMBATAS FOOD");
        // }
        // var direction2NearestPlayer = nearestPlayer.isEmpty() ? null : getHeadingBetween(nearestPlayer.get(0));
        // var direction2NearestFood = nearestFood.isEmpty() ? null : getHeadingBetween(nearestFood.get(0));
        
        // for(int i=0; i<nearestFood1.size(); i++){
        //     nearestFood2 = gameState.getGameObjects().stream().filter(item2 -> item2.getGameObjectType() == ObjectTypes.FOOD).sorted(Comparator.comparing(item2 -> getDistanceBetween(nearestFood1.get(i), item2))).collect(Collectors.toList());

        //     for(int j=0; j<nearestFood2.size(); j++){
                
        //     }

        // }

            

        if(nearestPlayer.isEmpty()){
            System.out.println("Wa a a a");
        }
        var direction2NearestPlayer = getHeadingBetween(nearestPlayer.get(0));
        var direction2NearestSuperFood = getHeadingBetween((nearestSuperFood.get(0)));
        var direction2NearestFood = getHeadingBetween(nearestFood.get(0));

        if(getDistanceBetween(nearestFood.get(0), bot) < 0.8*getDistanceBetween(nearestSuperFood.get(0), bot)){
            fixfood = nearestFood.get(0);
        } else {
            fixfood = nearestSuperFood.get(0);
        }

        if(getDistanceBetween(fixfood, bot) < getDistanceBetween(neareestGasCloud.get(0), bot)){

            //bot kita lebih kecil
            if(nearestPlayer.get(0).getSize() >= bot.getSize()){
                System.out.println("=====");
                System.out.println(("UKURAN TARGET : " + nearestPlayer.get(0).getSize()));
                System.out.println(("UKURAN BOT : " + bot.getSize()));
                System.out.println(("JARAK TARGET DENGAN TEST : " + getDistanceBetween(nearestPlayer.get(0), bot)));
                System.out.println("=====");
                
                if(getDistanceBetween(nearestPlayer.get(0), bot) < (8*bot.getSize())){
                    udah = false;
                    System.out.println("TEST BOT KABUR");
                    heading = GetOppositeDirection(nearestPlayer.get(0), bot);
                    target = fixfood;
                    

                    // heading = direction2NearestFood;
                        targetIsPlayer = false;
                    System.out.println("INI OPPOSITE DIKEJAR" + heading);
                    count++;
                }
                else {
                    heading = getHeadingBetween(fixfood);
                    target = fixfood;
                    targetIsPlayer = false;
                    System.out.println("TEST BOT GOING FOR FEEDING");
                }
                
            }
            
            //bot kita lebih gede
            else if (2*nearestPlayer.get(0).getSize() < bot.getSize()){
                udah = false;
                if(getDistanceBetween(neareestGasCloud.get(0), bot) < 4*bot.getSize()){
                    heading = GetOppositeDirection2(neareestGasCloud.get(0), bot);
                    System.out.println("TEST BOT CHASING TARGET BUT GAS CLOUD");
                }
                else{
                    if(getDistanceBetween(target, bot) < (3*bot.getSize())){
                        heading = direction2NearestPlayer;
                        target = nearestPlayer.get(0);
                        targetIsPlayer = true;
                        System.out.println("TEST BOT CHASING SMALLER PLAYER");
                    }else {
                        heading = getHeadingBetween(fixfood);
                        target = fixfood;
                        targetIsPlayer = false;
                        System.out.println("TEST BOT GOING FOR FEEDING");
                    }
                }
            }
            else if(fixfood != null){
                heading = getHeadingBetween(fixfood);
                target = fixfood;
                targetIsPlayer = false;
                System.out.println("TEST BOT GOING FOR FEEDING");
            }
            else{
                target = fixfood;
                heading = getHeadingBetween(worldCenter);
                targetIsPlayer = false;
                System.out.println("TEST BOT COULDNT FIND ANYTHING");
            }
        } else{//ini tiati
            if (getDistanceBetween(nearestPlayer.get(0), bot) < (8*bot.getSize())){
                heading = GetOppositeDirection2(nearestPlayer.get(0), bot);
                target = fixfood;
                System.out.println("ADA GAS CLOUD TAPI ADA TARGET, KABUR DARI PLAYER ");
            } else if (getDistanceBetween(neareestGasCloud.get(0), bot) < 4*bot.getSize()){
                heading = GetOppositeDirection(neareestGasCloud.get(0), bot);
                target = fixfood;
                System.out.println("TEST NGEHINDAR GAS CLOUD MASIH JAUH ");
            }
            else{
                heading = GetOppositeDirection2(neareestGasCloud.get(0), bot);
                target = fixfood;
                System.out.println("TEST NGEHINDAR GAS CLOUD ");
            }
            
        }
        System.out.println("RETURN RESOLVE : " + heading);
        if(target == worldCenter){
            heading = getHeadingBetween(fixfood);
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