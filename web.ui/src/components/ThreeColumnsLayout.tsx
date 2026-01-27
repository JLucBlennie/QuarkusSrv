type ThreeColumnsLayoutProps = {
    left: React.ReactNode;
    center: React.ReactNode;
    right: React.ReactNode;
}

export function ThreeColumnsLayout({ left, center, right }: ThreeColumnsLayoutProps) {
  return (
      <div className="flex flex-row w-full h-full gap-4 p-4">
          <div className="w-1/3 p-4 rounded-lg">
              {left}
          </div>
          <div className="w-1/3 p-4 rounded-lg">
              {center}
          </div>
          <div className="w-1/3 p-4 rounded-lg">
              {right}
          </div>
      </div>
  );
}