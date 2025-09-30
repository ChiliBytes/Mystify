import os
import sys
import re
from PIL import Image

def extraer_numeros(texto):
    """Extrae números del texto para ordenamiento numérico"""
    numeros = re.findall(r'\d+', texto)
    return int(numeros[0]) if numeros else texto

def convertir_imagenes_a_pdf(carpeta_imagenes, archivo_salida):
    """Convierte las imágenes de una carpeta a PDF, una imagen por página"""

    # Verificar si la carpeta existe
    if not os.path.exists(carpeta_imagenes):
        print(f"❌ Error: La carpeta '{carpeta_imagenes}' no existe.")
        return False

    # Extensiones de imagen válidas
    extensiones_validas = {'.jpg', '.jpeg', '.png', '.bmp', '.tiff', '.tif', '.webp', '.gif'}

    # Obtener lista de imágenes válidas
    imagenes = []
    for archivo in os.listdir(carpeta_imagenes):
        ruta_completa = os.path.join(carpeta_imagenes, archivo)
        if os.path.isfile(ruta_completa):
            extension = os.path.splitext(archivo)[1].lower()
            if extension in extensiones_validas:
                imagenes.append(archivo)

    if not imagenes:
        print("❌ No se encontraron imágenes válidas en la carpeta.")
        print(f"   Formatos soportados: {', '.join(extensiones_validas)}")
        return False

    # Ordenar las imágenes NUMÉRICAMENTE
    imagenes.sort(key=lambda x: [int(text) if text.isdigit() else text.lower()
                                for text in re.split(r'(\d+)', x)])

    print(f"📁 Carpeta: {carpeta_imagenes}")
    print(f"🖼️  Imágenes encontradas: {len(imagenes)}")
    print("📊 Orden de las imágenes:")
    for i, img in enumerate(imagenes, 1):
        print(f"   {i:2d}. {img}")
    print(f"💾 PDF de salida: {archivo_salida}")
    print("\n🔄 Procesando imágenes...")

    try:
        # Crear lista de objetos Image
        lista_imagenes = []
        for i, imagen in enumerate(imagenes, 1):
            ruta_imagen = os.path.join(carpeta_imagenes, imagen)
            img = Image.open(ruta_imagen)

            # Convertir a RGB si es necesario (para formato PDF)
            if img.mode != 'RGB':
                img = img.convert('RGB')

            lista_imagenes.append(img)
            print(f"   ✅ Procesada: {imagen} ({i}/{len(imagenes)})")

        # Guardar como PDF
        if lista_imagenes:
            lista_imagenes[0].save(
                archivo_salida,
                save_all=True,
                append_images=lista_imagenes[1:],
                resolution=100.0
            )

            print(f"\n✅ PDF creado exitosamente!")
            print(f"📄 Páginas: {len(imagenes)}")
            print(f"📁 Ubicación: {archivo_salida}")
            return True

    except Exception as e:
        print(f"❌ Error al crear el PDF: {str(e)}")
        return False
    finally:
        # Cerrar todas las imágenes
        for img in lista_imagenes:
            img.close()

def main():
    """Función principal"""
    print("🖼️  Conversor de imágenes a PDF (Orden Numérico)")
    print("=" * 50)

    if len(sys.argv) == 3:
        # Usar argumentos de línea de comandos
        carpeta_imagenes = sys.argv[1]
        archivo_salida = sys.argv[2]
    else:
        # Solicitar entrada al usuario
        print("\n📁 Ingresa la ruta de la carpeta con las imágenes:")
        carpeta_imagenes = input("> ").strip().strip('"')

        print("\n💾 Ingresa la ruta completa del PDF de salida (incluyendo .pdf):")
        archivo_salida = input("> ").strip().strip('"')

    # Asegurar que el archivo de salida tenga extensión .pdf
    if not archivo_salida.lower().endswith('.pdf'):
        archivo_salida += '.pdf'

    print()
    convertir_imagenes_a_pdf(carpeta_imagenes, archivo_salida)

if __name__ == "__main__":
    main()