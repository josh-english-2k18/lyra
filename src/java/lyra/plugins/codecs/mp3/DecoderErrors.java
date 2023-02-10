/*
 * 11/19/04		1.0 moved to LGPL.
 * 01/12/99		Initial version.	mdm@techie.com
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */

package lyra.plugins.codecs.mp3;

/**
 * This interface provides constants describing the error
 * codes used by the Decoder to indicate errors. 
 * 
 * @author	MDM
 */
public interface DecoderErrors extends JavaLayerErrors
{
	
	static public final int UNKNOWN_ERROR = DECODER_ERROR + 0;
	
	/**
	 * Layer not supported by the decoder. 
	 */
	static public final int UNSUPPORTED_LAYER = DECODER_ERROR + 1;
}