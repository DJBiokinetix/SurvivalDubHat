package com.survivaldub;

public enum SurvivalDubHatPerm
{
  HAT("hat"),  HAT_ITEMS("hat.items"),  HAT_GIVE_PLAYERS_ITEMS("hat.give.players.items"),  HAT_GIVE_GROUPS_ITEMS("hat.give.groups.items"),  HAT_RETURN("return"),  UNHAT("unhat");
  
  public final String node;
  
  private SurvivalDubHatPerm(String permissionNode)
  {
    this.node = ("blockhat." + permissionNode);
  }
}
