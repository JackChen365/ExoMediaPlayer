package org.cz.media.player.base.controller;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import org.cz.media.player.base.controller.component.PlayerComponent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by cz
 * @date 2020/9/9 11:36 AM
 * @email binigo110@126.com
 */
class MediaPlayerComponentManager {
    private final Map<String,PlayerComponent> playerComponents=new HashMap<>();

    @MainThread
    public void registerPlayerComponent(@Nullable PlayerComponent playerComponent){
        if(null!=playerComponent){
            String componentName = playerComponent.getComponentName();
            playerComponents.put(componentName,playerComponent);
        }
    }

    @MainThread
    public void unregisterPlayerComponent(@Nullable PlayerComponent playerComponent){
        if(null!=playerComponent){
            String componentName = playerComponent.getComponentName();
            playerComponents.remove(componentName);
        }
    }

    public <T extends PlayerComponent> T getPlayerComponent(@PlayerComponent.ComponentName String componentName){
        PlayerComponent playerComponent = playerComponents.get(componentName);
        return (T)playerComponent;
    }

    public void updateComponent(@PlayerComponent.ComponentName String componentName, MediaPlayerViewHolder viewHolder){
        PlayerComponent component = getPlayerComponent(componentName);
        if(null!=component){
            component.update(viewHolder);
        }
    }

    public void executeComponent(@PlayerComponent.ComponentName String componentName){
        PlayerComponent component = getPlayerComponent(componentName);
        if(null!=component){
            component.execute();
        }
    }

    public Collection<PlayerComponent> getPlayerComponentList(){
        return playerComponents.values();
    }

    public void clear(){
        this.playerComponents.clear();
    }
}
