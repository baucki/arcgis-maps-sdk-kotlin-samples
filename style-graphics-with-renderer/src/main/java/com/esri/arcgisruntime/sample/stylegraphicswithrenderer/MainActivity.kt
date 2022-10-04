/* Copyright 2022 Esri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.esri.arcgisruntime.sample.stylegraphicswithrenderer

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import arcgisruntime.ApiKey
import arcgisruntime.ArcGISRuntimeEnvironment
import arcgisruntime.geometry.CubicBezierSegment
import arcgisruntime.geometry.EllipticArcSegment
import arcgisruntime.geometry.Geometry
import arcgisruntime.geometry.Point
import arcgisruntime.geometry.Polygon
import arcgisruntime.geometry.PolygonBuilder
import arcgisruntime.geometry.PolylineBuilder
import arcgisruntime.geometry.SpatialReference
import arcgisruntime.mapping.ArcGISMap
import arcgisruntime.mapping.BasemapStyle
import arcgisruntime.mapping.Viewpoint
import arcgisruntime.mapping.symbology.SimpleFillSymbol
import arcgisruntime.mapping.symbology.SimpleFillSymbolStyle
import arcgisruntime.mapping.symbology.SimpleLineSymbol
import arcgisruntime.mapping.symbology.SimpleLineSymbolStyle
import arcgisruntime.mapping.symbology.SimpleMarkerSymbol
import arcgisruntime.mapping.symbology.SimpleMarkerSymbolStyle
import arcgisruntime.mapping.symbology.SimpleRenderer
import arcgisruntime.mapping.view.Graphic
import arcgisruntime.mapping.view.GraphicsOverlay
import arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.sample.stylegraphicswithrenderer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    // set up data binding for the activity
    private val activityMainBinding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    private val mapView by lazy {
        activityMainBinding.mapView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // authentication with an API key or named user is
        // required to access basemaps and other location services
        ArcGISRuntimeEnvironment.apiKey = ApiKey.create(BuildConfig.API_KEY)
        lifecycle.addObserver(mapView)

        //  add a map with a topographic night basemap style
        mapView.map = ArcGISMap(BasemapStyle.ArcGISTopographic)
        mapView.setViewpoint(Viewpoint(15.169193, 16.333479, 100000000.0))

        // add graphics overlays
        mapView.graphicsOverlays.addAll(
            arrayOf(
                renderedPointGraphicsOverlay(),
                renderedLineGraphicsOverlay(),
                renderedPolygonGraphicsOverlay(),
                renderedCurvedPolygonGraphicsOverlay(),
                renderedEllipseGraphicsOverlay()
            )
        )
    }

    /**
     * Create a point, its graphic, a graphics overlay for it, and add it to the map view.
     */
    private fun renderedPointGraphicsOverlay(): GraphicsOverlay {
        // create point
        val pointGeometry = Point(40e5, 40e5, SpatialReference.webMercator())
        // create graphic for point
        val pointGraphic = Graphic(pointGeometry)
        // red diamond point symbol
        val pointSymbol =
            SimpleMarkerSymbol(SimpleMarkerSymbolStyle.Diamond, Color.RED, 10f)
        // create simple renderer
        val pointRenderer = SimpleRenderer(pointSymbol)
        // create a new graphics overlay with these settings and add it to the map view
        return GraphicsOverlay().apply {
            // add graphic to overlay
            graphics.add(pointGraphic)
            // set the renderer on the graphics overlay to the new renderer
            renderer = pointRenderer
        }
    }

    /**
     * Create a polyline, its graphic, a graphics overlay for it, and add it to the map view.
     */
    private fun renderedLineGraphicsOverlay(): GraphicsOverlay {
        // create line
        val lineGeometry = PolylineBuilder(SpatialReference.webMercator()).apply {
            addPoint(-10e5, 40e5)
            addPoint(20e5, 50e5)
        }
        // create graphic for polyline
        val lineGraphic = Graphic(lineGeometry.toGeometry())
        // solid blue line symbol
        val lineSymbol =
            SimpleLineSymbol(SimpleLineSymbolStyle.Solid, Color.BLUE, 5f)
        // create simple renderer
        val lineRenderer = SimpleRenderer(lineSymbol)

        // create graphic overlay for polyline and add it to the map view
        return GraphicsOverlay().apply {
            // add graphic to overlay
            graphics.add(lineGraphic)
            // set the renderer on the graphics overlay to the new renderer
            renderer = lineRenderer
        }
    }

    /**
     * Create a polygon, its graphic, a graphics overlay for it, and add it to the map view.
     */
    private fun renderedPolygonGraphicsOverlay(): GraphicsOverlay {
        // create polygon
        val polygonGeometry = PolygonBuilder(SpatialReference.webMercator()).apply {
            addPoint(-20e5, 20e5)
            addPoint(20e5, 20e5)
            addPoint(20e5, -20e5)
            addPoint(-20e5, -20e5)
        }
        // create graphic for polygon
        val polygonGraphic = Graphic(polygonGeometry.toGeometry())
        // solid yellow polygon symbol
        val polygonSymbol =
            SimpleFillSymbol(SimpleFillSymbolStyle.Solid, Color.YELLOW, null)
        // create simple renderer
        val polygonRenderer = SimpleRenderer(polygonSymbol)

        // create graphic overlay for polygon and add it to the map view
        return GraphicsOverlay().apply {
            // add graphic to overlay
            graphics.add(polygonGraphic)
            // set the renderer on the graphics overlay to the new renderer
            renderer = polygonRenderer
        }
    }

    /**
     * Create a polygon, its graphic, a graphics overlay for it, and add it to the map view.
     */
    private fun renderedCurvedPolygonGraphicsOverlay(): GraphicsOverlay {
        // create a point for the center of the geometry
        val originPoint = Point(40e5, 5e5, SpatialReference.webMercator())
        // create polygon
        val curvedPolygonGeometry = makeHeartGeometry(originPoint, 10e5)
        // create graphic for polygon
        val polygonGraphic = Graphic(curvedPolygonGeometry)
        // create a simple fill symbol with outline
        val curvedLineSymbol = SimpleLineSymbol(SimpleLineSymbolStyle.Solid, Color.BLACK, 1f)
        val curvedFillSymbol =
            SimpleFillSymbol(SimpleFillSymbolStyle.Solid, Color.RED, curvedLineSymbol)
        // create simple renderer
        val polygonRenderer = SimpleRenderer(curvedFillSymbol)

        // create graphic overlay for polygon and add it to the map view
        return GraphicsOverlay().apply {
            // add graphic to overlay
            graphics.add(polygonGraphic)
            // set the renderer on the graphics overlay to the new renderer
            renderer = polygonRenderer
        }
    }

    /**
     * Create a heart-shape geometry with Bezier and elliptic arc segments from a given [center]
     * point and [sideLength].
     */
    private fun makeHeartGeometry(center: Point, sideLength: Double): Geometry {
        val spatialReference = center.spatialReference
        // the x and y coordinates to simplify the calculation
        val minX = center.x - 0.5 * sideLength
        val minY = center.y - 0.5 * sideLength
        // the radius of the arcs
        val arcRadius = sideLength * 0.25

        // bottom left curve
        val leftCurveStart = Point(center.x, minY, spatialReference)
        val leftCurveEnd = Point(minX, minY + 0.75 * sideLength, spatialReference)
        val leftControlPoint1 = Point(center.x, minY + 0.25 * sideLength, spatialReference)
        val leftControlPoint2 = Point(minX, center.y, spatialReference)
        val leftCurve = CubicBezierSegment(
            leftCurveStart,
            leftControlPoint1,
            leftControlPoint2,
            leftCurveEnd,
            spatialReference
        )

        // top left arc
        val leftArcCenter =
            Point(minX + 0.25 * sideLength, minY + 0.75 * sideLength, spatialReference)
        val leftArc = EllipticArcSegment.createCircularEllipticArc(
            leftArcCenter,
            arcRadius,
            Math.PI,
            -Math.PI,
            spatialReference
        )

        // top right arc
        val rightArcCenter =
            Point(minX + 0.75 * sideLength, minY + 0.75 * sideLength, spatialReference)
        val rightArc = EllipticArcSegment.createCircularEllipticArc(
            rightArcCenter,
            arcRadius,
            Math.PI,
            -Math.PI,
            spatialReference
        )

        // bottom right curve
        val rightCurveStart = Point(minX + sideLength, minY + 0.75 * sideLength, spatialReference)
        val rightCurveEnd = leftCurveStart
        val rightControlPoint1 = Point(minX + sideLength, center.y, spatialReference)
        val rightControlPoint2 = leftControlPoint1
        val rightCurve = CubicBezierSegment(
            rightCurveStart,
            rightControlPoint1,
            rightControlPoint2,
            rightCurveEnd,
            spatialReference
        )

        // TODO: Part need to be implemented
        val heart = Part(spatialReference).apply {
            add(leftCurve)
            add(leftArc)
            add(rightArc)
            add(rightCurve)
        }
        return Polygon(heart, spatialReference)
    }


    private fun renderedEllipseGraphicsOverlay(): GraphicsOverlay {

    }
}
