package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;

/**
 * Enregistrement qui représente un point de passage.
 *
 * @param pointCh le point en coordonnée PointCh à partir duquel le WayPoint est créé
 * @param nodeId identité du nœud associé au Waypoint
 *
 * @author Camille Espieux (324248)
 * @author Chiara Freneix (329552)
 */
public record Waypoint(PointCh pointCh, int nodeId) {}
