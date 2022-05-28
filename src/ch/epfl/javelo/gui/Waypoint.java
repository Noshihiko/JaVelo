package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement qui représente un point de passage.
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record Waypoint(PointCh pointCh, int nodeId){}
