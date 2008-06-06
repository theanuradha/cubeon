/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.cubeon.ui.util;

import java.awt.Color;
import java.awt.GradientPaint;

/**
 *
 * @author Anuradha
 */
public class GradientUtil { 

    public static GradientPaint GRADIENT_HEADER_LARGE = new GradientPaint(700, 0,
            Color.WHITE, 800, 0, new Color(0xEBF4FA));
    public static GradientPaint GRADIENT_HEADER_NOMAL = new GradientPaint(500, 0,
            Color.WHITE, 600, 0, new Color(0xEBF4FA));
    public static GradientPaint GRADIENT_HEADER_COLOR_PALETTE = new GradientPaint(50, 0,
            new Color(181, 198, 217), 215, 0, java.awt.SystemColor.control);
    public static GradientPaint GRADIENT_HEADER_COLOR_SMALL = new GradientPaint(200, 0,
            new Color(181, 198, 217), 400, 0, Color.WHITE);
    public static GradientPaint GRADIENT_HEADER_COLOR_LARGE = new GradientPaint(100, 0,
            new Color(181, 198, 217), 800, 0, Color.WHITE);
}
